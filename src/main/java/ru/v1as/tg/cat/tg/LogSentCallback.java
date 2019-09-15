package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@Slf4j
public class LogSentCallback<T extends Serializable> implements SentCallback<T> {

    @Override
    public void onResult(BotApiMethod<T> method, T response) {
        log.debug("Success callback :{}", response);
    }

    @Override
    public void onError(BotApiMethod<T> method, TelegramApiRequestException apiException) {
        log.warn("Error callback.", apiException);
    }

    @Override
    public void onException(BotApiMethod<T> method, Exception exception) {
        log.error("Exception callback.", exception);
    }
}
