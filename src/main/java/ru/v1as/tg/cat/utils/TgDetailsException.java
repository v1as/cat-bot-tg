package ru.v1as.tg.cat.utils;

import lombok.experimental.Delegate;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;

public class TgDetailsException extends TelegramApiRequestException {

    @Delegate private final TelegramApiRequestException cause;

    private TgDetailsException(String message, TelegramApiRequestException cause) {
        super(message, cause);
        this.cause = cause;
    }

    @SuppressWarnings("unchecked")
    public static <T extends Throwable> T tgException(T ex) {
        if (ex instanceof TelegramApiRequestException) {
            TelegramApiRequestException tEx = (TelegramApiRequestException) ex;
            final String message =
                    String.format(
                            "Poll error: message '%s' code '%s'",
                            tEx.getApiResponse(), tEx.getErrorCode());
            final TgDetailsException result = new TgDetailsException(message, tEx);
            return (T) result;
        } else {
            return ex;
        }
    }
}
