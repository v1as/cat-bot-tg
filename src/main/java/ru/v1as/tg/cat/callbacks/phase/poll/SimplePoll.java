package ru.v1as.tg.cat.callbacks.phase.poll;

import static java.util.Collections.singletonList;
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
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.v1as.tg.cat.callbacks.SimpleCallbackHandler;
import ru.v1as.tg.cat.callbacks.TgCallBackHandler;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
public class SimplePoll {

    private static final ScheduledExecutorService EXECUTOR = new ScheduledThreadPoolExecutor(1);

    private UnsafeAbsSender sender;
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
    private CloseOnTextBuilder closeOnTextBuilder = new DefaultCloseTextBuilder();

    private PhaseContext phaseContext;
    private ThreadLocal<PhaseContext> phaseContextThreadLocal;

    public SimplePoll choice(PollChoice choice) {
        choices.putIfAbsent(choice.getUuid(), choice);
        return this;
    }

    public SimplePoll choice(String text, Consumer<ChooseContext> method) {
        String callback = generateCallback();
        return choice(
                new PollChoice(
                        callback,
                        PollChoiceType.TEXT,
                        text,
                        null,
                        buildChoseContextConsumer(method)));
    }

    private Consumer<ChooseContext> buildChoseContextConsumer(Consumer<ChooseContext> method) {
        return (ctx) -> {
            phaseContextThreadLocal.set(phaseContext);
            try {
                method.accept(ctx);
            } finally {
                phaseContextThreadLocal.remove();
            }
        };
    }

    public SimplePoll text(String text) {
        this.text = text;
        return this;
    }

    protected String generateCallback() {
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
        sender.executeAsyncPromise(message, this::pollMessageSent, this::pollMessageFail);
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
            EXECUTOR.schedule(
                    () -> {
                        if (state.equals(SENT)) {
                            this.close(timeoutConfiguration.removeMsg());
                            if (timeoutConfiguration.onTimeout() != null) {
                                timeoutConfiguration.onTimeout().run();
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
                this.onSend.accept(sent);
            } catch (Exception e) {
                log.error("Error while onSend callback.");
            }
        }
    }

    private TgCallBackHandler callbackHandler(PollChoice pollChoice) {
        return new SimpleCallbackHandler(pollChoice.getUuid()) {
            @Override
            public void handle(String value, Chat chat, User user, CallbackQuery callbackQuery) {
                choose = choices.get(callbackQuery.getData());
                if (closeOnChoose) {
                    close();
                }
                pollChoice.getCallable().accept(new ChooseContext(chat, user, callbackQuery));
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
                sender.executeUnsafe(deleteMsg(message));
            } else {
                String newText =
                        closeOnTextBuilder.build(text, choose != null ? choose.getText() : null);
                if (!Objects.equals(newText, text)) {
                    sender.executeUnsafe(editMessageText(message, newText));
                } else {
                    sender.executeUnsafe(clearButtons(message));
                }
            }
        } else {
            log.error("Can't close poll, illegal state: {}", this);
        }
    }

    public void cancel() {
        if (state.equals(SENT)) {
            state = CANCELED;
            sender.executeUnsafe(deleteMsg(message));
        } else {
            log.error("Can't close poll, illegal state: {}", this);
        }
    }

    public SimplePoll closeOnChoose(boolean closeOnChoose) {
        return this;
    }

    public SimplePoll closeTextBuilder(@NonNull DefaultCloseTextBuilder defaultCloseTextBuilder) {
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

    public SimplePoll removeOnChoice(boolean value) {
        this.removeOnClose = value;
        return this;
    }

    public void setSender(UnsafeAbsSender sender) {
        this.sender = sender;
    }

    public void setCallbackProcessor(TgCallbackProcessor callbackProcessor) {
        this.callbackProcessor = callbackProcessor;
    }

    public void setPhaseContext(ThreadLocal<? extends PhaseContext> phaseContext) {
        this.phaseContextThreadLocal = (ThreadLocal<PhaseContext>) phaseContext;
        this.phaseContext = phaseContext.get();
    }

}
