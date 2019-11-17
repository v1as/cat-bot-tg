package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

public interface TgSender {

    <T extends Serializable, Method extends BotApiMethod<T>, Callback extends SentCallback<T>>
            void executeTgAsync(Method method, Callback callback);

    default <T extends Serializable, Method extends BotApiMethod<T>> void executeAsyncPromise(
            Method method, Consumer<T> success, Consumer<Throwable> error) {
        executeTgAsync(
                method,
                new LogSentCallback<T>() {
                    @Override
                    public void onResult(BotApiMethod<T> method, T response) {
                        super.onResult(method, response);
                        success.accept(response);
                    }

                    @Override
                    public void onError(
                            BotApiMethod<T> method, TelegramApiRequestException apiException) {
                        super.onError(method, apiException);
                        error.accept(apiException);
                    }

                    @Override
                    public void onException(BotApiMethod<T> method, Exception exception) {
                        super.onException(method, exception);
                        error.accept(exception);
                    }
                });
    }

    <T extends Serializable, Method extends BotApiMethod<T>> T executeTg(Method method);

    void setSender(AbsSender sender);

    Message executeTg(SendDocument sendDocument);

}
