package ru.v1as.tg.cat.messages;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageHandler {

    void handle(Message message, Chat chat, User user);
}
