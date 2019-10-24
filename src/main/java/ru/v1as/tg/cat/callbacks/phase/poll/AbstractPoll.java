package ru.v1as.tg.cat.callbacks.phase.poll;

import static java.util.Collections.singletonList;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CANCELED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CLOSED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.CREATED;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.ERROR;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENDING;
import static ru.v1as.tg.cat.callbacks.phase.poll.State.SENT;
import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.editMessageText;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.Value;
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
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPoll<T extends AbstractPoll<T>> {

    private final UnsafeAbsSender sender;
    private final TgCallbackProcessor callbackProcessor;

    private Long chatId;
    private State state = CREATED;
    private String text;

    private Map<String, PollChoice> choices = new LinkedHashMap<>();
    private PollChoice choose;

    private Message message;
    private List<Consumer<Message>> onSendCallbacks;

    private PollTimeoutConfiguration timeoutConfiguration;

    private boolean removeOnChoice = false;
    private boolean closeOnChoose = true;
    private CloseOnTextBuilder closeOnTextBuilder = new DefaultCloseTextBuilder();

    public T choice(String text, Runnable method) {
        String callBackData = generateCallback();
        choices.putIfAbsent(
                callBackData,
                new PollChoice(callBackData, PollChoiceType.TEXT, text, null, method));
        return self();
    }

    public T choice(String text, Consumer<UserData> method) {
        return choice(text, () -> method.accept(null));
    }

    public T text(String text) {
        this.text = text;
        return self();
    }

    public T choiceLink(String text, String url, Runnable method) {
        String callBackData = generateCallback();
        choices.putIfAbsent(
                callBackData, new PollChoice(callBackData, PollChoiceType.LINK, text, url, method));
        return self();
    }

    protected String generateCallback() {
        return UUID.randomUUID().toString();
    }

    public T reset() {
        choices.clear();
        throw new RuntimeException("Unsupported operation yet");
    }

    public T addOnSend(Consumer<Message> callback) {
        this.onSendCallbacks.add(callback);
        return self();
    }

    protected abstract T self();

    public T send() {
        InlineKeyboardMarkup replyMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboardLines = new ArrayList<>();
        replyMarkup.setKeyboard(keyboardLines);
        this.state = SENDING;
        SendMessage message = new SendMessage(chatId, text).setReplyMarkup(replyMarkup);
        for (PollChoice c : choices.values()) {
            if (c.getType().equals(PollChoiceType.TEXT)) {
                keyboardLines.add(
                        singletonList(new InlineKeyboardButton(c.text).setCallbackData(c.uuid)));
            }
        }
        sender.executeAsyncPromise(message, this::pollMessageSent, this::pollMessageFail);
        return self();
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
        for (Consumer<Message> c : this.onSendCallbacks) {
            try {
                c.accept(sent);
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
                pollChoice.getRunnable().run();
            }
        };
    }

    public void close() {
        if (state.equals(SENT)) {
            state = CLOSED;
            String newText = closeOnTextBuilder.build(text, choose.getText());
            if (!Objects.equals(newText, text)) {
                sender.executeUnsafe(editMessageText(message, newText));
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

    public T closeOnChoose(boolean closeOnChoose) {
        return self();
    }

    public T closeTextBuilder(DefaultCloseTextBuilder defaultCloseTextBuilder) {
        this.closeOnTextBuilder = closeOnTextBuilder;
        return self();
    }

    public T chatId(Long chatId) {
        this.chatId = chatId;
        return self();
    }

    public T timeout(PollTimeoutConfiguration timeoutConfiguration) {
        this.timeoutConfiguration = timeoutConfiguration;
        return self();
    }

    public T removeOnChoice(boolean value) {
        this.removeOnChoice = value;
        return self();
    }

    @Value
    private class PollChoice {
        private final String uuid;
        private final PollChoiceType type;
        private final String text;
        private final String url;
        private final Runnable runnable;
    }

    private enum PollChoiceType {
        TEXT,
        LINK
    }
}
