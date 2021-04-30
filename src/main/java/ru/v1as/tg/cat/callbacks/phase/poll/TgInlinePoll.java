package ru.v1as.tg.cat.callbacks.phase.poll;

import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CANCELED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CLOSED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CREATED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.ERROR;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENDING;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENT;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.UPDATED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.UPDATING;
import static ru.v1as.tg.cat.tg.KeyboardUtils.clearButtons;
import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.editMessageText;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.callbacks.SimpleCallbackHandler;
import ru.v1as.tg.cat.callbacks.TgCallBackHandler;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.PhaseContextClosedException;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.ChoiceAroundInterceptor;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.NoopChoiceAroundInterceptor;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
public class TgInlinePoll {

    private BotClock botClock;
    private TgSender sender;
    private TgCallbackProcessor callbackProcessor;

    private Long chatId;
    private State state = CREATED;
    private String text;

    private Map<String, PollChoice> choices = new LinkedHashMap<>();
    private PollChoice choose;

    private Message message;
    private Consumer<Message> onSend = null;

    private PollTimeoutConfiguration timeoutConfiguration;

    private boolean removeOnClose = false;
    private boolean closeOnChoose = true;
    private CloseOnTextBuilder closeOnTextBuilder = new NopeCloseTextBuilder();
    private ChoiceAroundInterceptor choiceAroundInterceptor = new NoopChoiceAroundInterceptor();
    private Sent sent;

    public TgInlinePoll choice(PollChoice choice) {
        checkModifiable();
        choices.putIfAbsent(choice.getUuid(), choice);
        return this;
    }

    public List<PollChoice> getChoices() {
        return new ArrayList<>(choices.values());
    }

    public TgInlinePoll choice(String text, Consumer<ChooseContext> method) {
        checkModifiable();
        String callback = generateCallback(text);
        return choice(
                new PollChoice(
                        callback,
                        PollChoiceType.TEXT,
                        text,
                        null,
                        ctx -> choiceAroundInterceptor.around(ctx, method)));
    }

    public TgInlinePoll text(String text) {
        checkModifiable();
        this.text = text;
        return this;
    }

    public String text() {
        return this.text;
    }

    private String generateCallback(String text) {
        return UUID.randomUUID().toString();
    }

    public TgInlinePoll clearChoices() {
        checkModifiable();
        releaseResources();
        choices.clear();
        return this;
    }

    private void releaseResources() {
        this.choices.values().forEach(pollChoice -> callbackProcessor.drop(pollChoice.getUuid()));
    }

    public TgInlinePoll onSend(Consumer<Message> callback) {
        this.onSend = callback;
        return this;
    }

    public TgInlinePoll send() {
        if (CREATED.equals(state)) {
            this.state = SENDING;
            SendMessage message = new SendMessage(chatId, text).setReplyMarkup(getKeyboard());
            String choices =
                    this.choices.values().stream()
                            .map(PollChoice::getText)
                            .collect(Collectors.joining("/", "[", "]"));
            this.sent = new Sent(this.text, getChoices());
            sender.executeAsyncPromise(message, this::pollMessageSent, this::pollMessageFail);
            log.info("Poll '{}' send to chat '{}'", text + choices, chatId);
        } else if (SENT.equals(state) || UPDATED.equals(state)) {
            EditMessageText editMessage =
                    new EditMessageText()
                            .setMessageId(this.message.getMessageId())
                            .setChatId(chatId)
                            .setText(this.text)
                            .setReplyMarkup(getKeyboard());
            Sent newSent = new Sent(text, getChoices());
            if (sent.changed(newSent)) {
                state = UPDATING;
                sender.executeAsyncPromise(
                        editMessage, this.pollMessageUpdated(newSent), this::pollMessageFail);
            }
        } else {
            throw new IllegalStateException("This poll can't be send because of state: " + state);
        }
        return this;
    }

    private <T extends Serializable> Consumer<T> pollMessageUpdated(final Sent newSent) {
        return (v) -> {
            this.sent = newSent;
            pollMessageSent(message);
        };
    }

    private InlineKeyboardMarkup getKeyboard() {
        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardLines = new ArrayList<>();
        for (PollChoice c : choices.values()) {
            InlineKeyboardButton button;
            if (c.getType().equals(PollChoiceType.TEXT)) {
                button = new InlineKeyboardButton(c.getText()).setCallbackData(c.getUuid());
            } else if (PollChoiceType.LINK.equals(c.getType())) {
                button = new InlineKeyboardButton(c.getText()).setUrl(c.getUrl());
            } else {
                throw new IllegalArgumentException();
            }
            keyboardLines.add(singletonList(button));
        }
        replyMarkup.setKeyboard(keyboardLines);
        return replyMarkup;
    }

    private void pollMessageFail(Throwable throwable) {
        log.error("Poll error", throwable);
        this.state = ERROR;
    }

    private void pollMessageSent(Message sent) {
        this.choices
                .values()
                .forEach(pollChoice -> callbackProcessor.register(callbackHandler(pollChoice)));
        if (state.equals(SENDING)) {
            this.message = sent;
            this.state = SENT;
            timeoutProcess();
            processOnSend(sent);
        } else if (UPDATING.equals(state)) {
            this.state = UPDATED;
        }
    }

    private void timeoutProcess() {
        if (timeoutConfiguration != null) {
            botClock.schedule(
                    () -> {
                        if (state.equals(SENT)) {

                            this.close(timeoutConfiguration.removeMsg());

                            if (!isEmpty(timeoutConfiguration.message())) {
                                sender.executeAsync(
                                        new SendMessage(chatId, timeoutConfiguration.message()));
                            }

                            if (timeoutConfiguration.onTimeout() != null) {
                                choiceAroundInterceptor.around(
                                        null, ctx -> timeoutConfiguration.onTimeout().run());
                            }
                        }
                    },
                    timeoutConfiguration.delay().toNanos(),
                    TimeUnit.NANOSECONDS);
        }
    }

    private void processOnSend(Message sent) {
        if (this.onSend != null) {
            try {
                this.choiceAroundInterceptor.around(null, nullCtx -> this.onSend.accept(sent));
            } catch (Exception e) {
                log.error("Error while onSend callback.", e);
            }
        }
    }

    private TgCallBackHandler<?> callbackHandler(PollChoice pollChoice) {
        return new SimpleCallbackHandler(pollChoice.getUuid()) {
            @Override
            public void handle(
                    String value, TgChat chat, TgUser user, CallbackQuery callbackQuery) {
                choose = choices.get(callbackQuery.getData());
                log.info(" choose '{}'", choose.getText());
                if (closeOnChoose) {
                    close();
                }
                try {
                    pollChoice.getCallable().accept(new ChooseContext(chat, user, callbackQuery));
                } catch (PhaseContextClosedException closed) {
                    log.debug("Context is closed already. Nothing to do.");
                }
            }
        };
    }

    public void close() {
        close(this.removeOnClose);
    }

    private void close(boolean shouldRemove) {
        releaseResources();
        if (state.equals(SENT) || state.equals(UPDATED)) {
            state = CLOSED;
            if (shouldRemove) {
                sender.executeAsync(deleteMsg(message));
            } else {
                String newText =
                        closeOnTextBuilder.build(text, choose != null ? choose.getText() : null);
                if (!Objects.equals(newText, text)) {
                    sender.executeAsync(editMessageText(message, newText));
                } else {
                    sender.executeAsync(clearButtons(message));
                }
            }
        }
    }

    public void cancel() {
        clearChoices();
        if (state.equals(SENT) || state.equals(UPDATED)) {
            state = CANCELED;
            sender.executeAsync(deleteMsg(message));
        } else if (state.equals(CREATED)) {
            state = CANCELED;
        }
    }

    public TgInlinePoll closeOnChoose(boolean closeOnChoose) {
        checkModifiable();
        this.closeOnChoose = closeOnChoose;
        return this;
    }

    public TgInlinePoll closeTextBuilder(@NonNull CloseOnTextBuilder closeOnTextBuilder) {
        checkModifiable();
        this.closeOnTextBuilder = closeOnTextBuilder;
        return this;
    }

    public TgInlinePoll chatId(Long chatId) {
        checkCreated();
        this.chatId = chatId;
        return this;
    }

    public TgInlinePoll timeout(PollTimeoutConfiguration timeoutConfiguration) {
        checkModifiable();
        this.timeoutConfiguration = timeoutConfiguration;
        return this;
    }

    public TgInlinePoll removeOnClose(boolean value) {
        checkModifiable();
        this.removeOnClose = value;
        return this;
    }

    public void setBotClock(BotClock botClock) {
        checkCreated();
        this.botClock = botClock;
    }

    public void setSender(TgSender sender) {
        checkCreated();
        this.sender = sender;
    }

    public void setCallbackProcessor(TgCallbackProcessor callbackProcessor) {
        checkCreated();
        this.callbackProcessor = callbackProcessor;
    }

    public TgInlinePoll choiceAroundInterceptor(
            @NonNull ChoiceAroundInterceptor choiceAroundInterceptor) {
        checkCreated();
        this.choiceAroundInterceptor = choiceAroundInterceptor;
        return this;
    }

    private void checkModifiable() {
        if (!CREATED.equals(state) && !SENT.equals(state) && !UPDATED.equals(state)) {
            throw new IllegalStateException("Wrong poll state: " + state);
        }
    }

    private void checkCreated() {
        if (!CREATED.equals(state)) {
            throw new IllegalStateException("Wrong poll state: " + state);
        }
    }

    @Value
    private static class Sent {
        String text;
        List<PollChoice> choices;

        boolean changed(Sent sent) {
            return !Objects.equals(this.text, sent.text) || !Objects.equals(choices, sent.choices);
        }
    }
}
