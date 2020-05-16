package ru.v1as.tg.cat.tg;

import java.io.Serializable;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@Slf4j
public class LogSentCallback<T extends Serializable> implements SentCallback<T> {

    protected final MdcTgContext mdcTgContext;

    public LogSentCallback() {
        mdcTgContext = MdcTgContext.fromCurrentMdc();
    }

    @Override
    public void onResult(BotApiMethod<T> method, T response) {
        try (final MdcTgContext ignored = mdcTgContext.apply()) {
            log.debug("Success callback '{}' for method '{}'", response, method);
        }
    }

    @Override
    public void onError(BotApiMethod<T> method, TelegramApiRequestException apiException) {
        try (final MdcTgContext ignored = mdcTgContext.apply()) {
            log.warn("Error callback." + method, apiException);
        }
    }

    @Override
    public void onException(BotApiMethod<T> method, Exception exception) {
        try (final MdcTgContext ignored = mdcTgContext.apply()) {
            log.error("Exception callback." + method, exception);
        }
    }
}
