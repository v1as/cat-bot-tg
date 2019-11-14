package ru.v1as.tg.cat.messages;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
@Slf4j
public class TgMessageProcessor {

    private final List<MessageHandler> handlers;

    public TgMessageProcessor(List<MessageHandler> handlers) {
        this.handlers =
                handlers.stream()
                        .sorted(Comparator.comparingInt(MessageHandler::priority))
                        .collect(Collectors.toList());
    }

    public void process(Message message, TgChat chat, TgUser user) {
        for (MessageHandler handler : handlers) {
            try {
                handler.handle(message, chat, user);
            } catch (InterruptMessageProcessing ex) {
                log.info("Message processing was interrupted by handler: {}", handler);
                break;
            }
        }
    }

    public static class InterruptMessageProcessing extends RuntimeException {}
}
