package ru.v1as.tg.cat.callbacks;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface DefaultTgCallbackHandler {

    void handle(TgChat chat, TgUser user, CallbackQuery callbackQuery);
}
