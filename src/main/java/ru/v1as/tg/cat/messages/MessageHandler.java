package ru.v1as.tg.cat.messages;

import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface MessageHandler {

    int DEFAULT_PRIORITY = 500;
    int MIN_PRIORITY = 1000;
    int TOP_PRIORITY = 100;

    void handle(Message message, TgChat chat, TgUser user);

    default int priority() {
        return DEFAULT_PRIORITY;
    }
}
