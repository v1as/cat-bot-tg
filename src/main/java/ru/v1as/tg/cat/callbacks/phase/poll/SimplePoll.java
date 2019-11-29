package ru.v1as.tg.cat.callbacks.phase.poll;

import static java.util.Collections.singletonList;
import static org.springframework.util.StringUtils.isEmpty;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CANCELED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CLOSED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CREATED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.ERROR;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENDING;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENT;
import static ru.v1as.tg.cat.tg.KeyboardUtils.clearButtons;
import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.editMessageText;

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
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
public class SimplePoll {

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

    public SimplePoll choice(PollChoice choice) {
        choices.putIfAbsent(choice.getUuid(), choice);
        return this;
    }

    public Map<String, PollChoice> getChoices() {
        return choices;
    }

    public SimplePoll choice(String text, Consumer<ChooseContext> method) {
        String callback = generateCallback(text);
        return choice(
                new PollChoice(
                        callback,
                        PollChoiceType.TEXT,
                        text,
                        null,
                        ctx -> choiceAroundInterceptor.around(ctx, method)));
    }

    public SimplePoll text(String text) {
        this.text = text;
        return this;
    }

    public String text() {
        return this.text;
    }

    protected String generateCallback(String text) {
        return UUID.randomUUID().toString();
    }

    public SimplePoll reset() {
        choices.clear();
        throw new RuntimeException("Unsupported operation yet");
    }

    public SimplePoll onSend(Consumer<Message> callback) {
        this.onSend = callback;
        return this;
    }

    public SimplePoll send() {
        this.state = SENDING;
        SendMessage message = new SendMessage(chatId, text).setReplyMarkup(getKeyboard());
        String choices =
                this.choices.values().stream()
                        .map(PollChoice::getText)
                        .collect(Collectors.joining("/", "[", "]"));
        sender.executeAsyncPromise(message, this::pollMessageSent, this::pollMessageFail);
        log.info("Poll '{}' send to chat '{}'", text + choices, chatId);
        return this;
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
        this.message = sent;
        this.state = SENT;
        this.choices
                .values()
                .forEach(pollChoice -> callbackProcessor.register(callbackHandler(pollChoice)));
        processOnSend(sent);
        timeoutProcess();
    }

    private void timeoutProcess() {
        if (timeoutConfiguration != null) {
            botClock.schedule(
                    () -> {
                        if (state.equals(SENT)) {

                            this.close(timeoutConfiguration.removeMsg());

                            if (!isEmpty(timeoutConfiguration.message())) {
                                sender.execute(
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

    private TgCallBackHandler callbackHandler(PollChoice pollChoice) {
        return new SimpleCallbackHandler(pollChoice.getUuid()) {
            @Override
            public void handle(
                    String value, TgChat chat, TgUser user, CallbackQuery callbackQuery) {
                choose = choices.get(callbackQuery.getData());
                log.info("{} just choose '{}' in {}", user, choose.getText(), chat);
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
        if (state.equals(SENT)) {
            state = CLOSED;
            if (shouldRemove) {
                sender.execute(deleteMsg(message));
            } else {
                String newText =
                        closeOnTextBuilder.build(text, choose != null ? choose.getText() : null);
                if (!Objects.equals(newText, text)) {
                    sender.execute(editMessageText(message, newText));
                } else {
                    sender.execute(clearButtons(message));
                }
            }
        }
    }

    public void cancel() {
        if (state.equals(SENT)) {
            state = CANCELED;
            sender.execute(deleteMsg(message));
        } else {
            log.error("Can't close poll, illegal state: {}", this);
        }
    }

    public SimplePoll closeOnChoose(boolean closeOnChoose) {
        this.closeOnChoose = closeOnChoose;
        return this;
    }

    public SimplePoll closeTextBuilder(@NonNull CloseOnTextBuilder closeOnTextBuilder) {
        this.closeOnTextBuilder = closeOnTextBuilder;
        return this;
    }

    public SimplePoll chatId(Long chatId) {
        this.chatId = chatId;
        return this;
    }

    public SimplePoll timeout(PollTimeoutConfiguration timeoutConfiguration) {
        this.timeoutConfiguration = timeoutConfiguration;
        return this;
    }

    public SimplePoll removeOnClose(boolean value) {
        this.removeOnClose = value;
        return this;
    }

    public void setBotClock(BotClock botClock) {
        this.botClock = botClock;
    }

    public void setSender(TgSender sender) {
        this.sender = sender;
    }

    public void setCallbackProcessor(TgCallbackProcessor callbackProcessor) {
        this.callbackProcessor = callbackProcessor;
    }

    public SimplePoll choiceAroundInterceptor(
            @NonNull ChoiceAroundInterceptor choiceAroundInterceptor) {
        this.choiceAroundInterceptor = choiceAroundInterceptor;
        return this;
    }
}
