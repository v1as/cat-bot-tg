package ru.v1as.tg.cat.callbacks;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface TgCallBackHandler<T> {

    void handle(T value, TgChat chat, TgUser user, CallbackQuery callbackQuery);

    String getPrefix();

    T parse(String value);
}
