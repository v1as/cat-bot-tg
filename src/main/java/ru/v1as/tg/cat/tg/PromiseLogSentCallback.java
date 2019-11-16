package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import java.util.function.Consumer;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class PromiseLogSentCallback<T extends Serializable> extends LogSentCallback<T> {

    private final Consumer<T> success;
    private final Consumer<Throwable> error;

    public PromiseLogSentCallback(Consumer<T> success, Consumer<Throwable> error) {
        this.success = success;
        this.error = error;
    }

    @Override
    public void onResult(BotApiMethod<T> method, T response) {
        super.onResult(method, response);
        success.accept(response);
    }

    @Override
    public void onError(BotApiMethod<T> method, TelegramApiRequestException apiException) {
        super.onError(method, apiException);
        error.accept(apiException);
    }

    @Override
    public void onException(BotApiMethod<T> method, Exception exception) {
        super.onException(method, exception);
        error.accept(exception);
    }
}
