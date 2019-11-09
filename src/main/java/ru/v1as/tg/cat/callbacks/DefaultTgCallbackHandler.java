package ru.v1as.tg.cat.callbacks;

import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public interface DefaultTgCallbackHandler {

    void handle(Chat chat, User user, CallbackQuery callbackQuery);

}
