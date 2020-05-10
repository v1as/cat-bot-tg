package ru.v1as.tg.cat.messages.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.messages.MessageHandler;
import ru.v1as.tg.cat.messages.TgMessageProcessor.InterruptMessageProcessing;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
@Component
public class RequestMessageHandler implements MessageHandler {

    private final Map<MessageRequestKey, MessageRequest> requests = new HashMap<>();

    public void addRequest(MessageRequest request) {
        requests.put(request.getHashKey(), request);
    }

    @Override
    public final void handle(Message message, TgChat chat, TgUser user) {
        clearExpiredRequests();
        final MessageRequestKey hashKey = new MessageRequestKey(message);
        if (!requests.containsKey(hashKey)) {
            return;
        }
        final MessageRequest request = requests.get(hashKey);
        if (!request.filter().test(message)) {
            return;
        }
        try {
            requests.remove(hashKey);
            request.onResponse().accept(message);
        } catch (Exception ex) {
            log.error("Error on message request response ", ex);
        }
        throw new InterruptMessageProcessing();
    }

    @Override
    public int priority() {
        return TOP_PRIORITY;
    }

    private void clearExpiredRequests() {
        final Set<MessageRequest> expired =
                requests.values().stream()
                        .filter(MessageRequest::isExpired)
                        .collect(Collectors.toSet());
        for (MessageRequest request : expired) {
            try {
                requests.remove(request.getHashKey());
                request.onTimeout().run();
            } catch (Exception ex) {
                log.error("Error on message request timeout ", ex);
            }

        }
    }
}
