package ru.v1as.tg.cat.messages;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageHandler {

    int DEFAULT_PRIORITY = 500;
    int MIN_PRIORITY = 1000;
    int TOP_PRIORITY = 100;

    void handle(Message message, Chat chat, User user);

    default int priority() {
        return DEFAULT_PRIORITY;
    }
}
