package ru.v1as.tg.cat.callbacks.phase.poll;

import lombok.Value;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

@Value
public class ChooseContext {
    Chat chat;
    User user;
    CallbackQuery callbackQuery;
}
