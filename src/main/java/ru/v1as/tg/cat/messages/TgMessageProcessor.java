package ru.v1as.tg.cat.messages;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Component
@RequiredArgsConstructor
public class TgMessageProcessor {

    private final List<MessageHandler> handlers;

    public TgMessageProcessor register(MessageHandler messageHandler) {
        handlers.add(messageHandler);
        return this;
    }

    public void process(Message message, Chat chat, User user) {
        for (MessageHandler handler : handlers) {
            handler.handle(message, chat, user);
        }
    }
}
