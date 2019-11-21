package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.model.TgChat;

public interface TgSender {

    <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>>
            void executeAsync(Method method, Callback callback);

    default <T extends Serializable, Method extends BotApiMethod<T>> void executeAsyncPromise(
            Method method, Consumer<T> success, Consumer<Throwable> error) {
        executeAsync(method, new PromiseLogSentCallback<>(success, error));
    }

    <T extends Serializable, Method extends BotApiMethod<T>> T execute(Method method);

    default Message message(TgChat chat, String text) {
        return execute(new SendMessage(chat.getId(), text));
    }

    Message executeDoc(SendDocument sendDocument);
}
