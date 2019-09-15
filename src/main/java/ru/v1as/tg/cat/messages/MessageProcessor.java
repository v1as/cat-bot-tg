package ru.v1as.tg.cat.messages;

import java.util.ArrayList;
import java.util.List;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public class MessageProcessor {

    private List<MessageHandler> handlers = new ArrayList<>();

    public MessageProcessor register(MessageHandler messageHandler) {
        handlers.add(messageHandler);
        return this;
    }

    public void process(Message message, Chat chat, User user) {
        for (MessageHandler handler : handlers) {
            handler.handle(message, chat, user);
        }
    }
}
