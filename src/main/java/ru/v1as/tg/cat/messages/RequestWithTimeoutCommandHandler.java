package ru.v1as.tg.cat.messages;

import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.BREAK;
import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.SKIPPED;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public abstract class RequestWithTimeoutCommandHandler implements MessageHandler {

    private final Set<MessageWaitingRequest> requests = new HashSet<>();

    public void addRequest(Message msg, Runnable timeout) {
        requests.add(new MessageWaitingRequest(msg, timeout));
    }

    @Override
    public final MessageHandlerResult handle(Message message, TgChat chat, TgUser user) {
        clearExpiredRequests();
        final MessageWaitingRequest request = new MessageWaitingRequest(message);
        if (!requests.contains(request)) {
            return SKIPPED;
        }
        if (this.handleRequest(message, chat, user)) {
            requests.remove(request);
            return BREAK;
        }
        return SKIPPED;
    }

    @Override
    public int priority() {
        return TOP_PRIORITY;
    }

    protected abstract boolean handleRequest(Message message, TgChat chat, TgUser user);

    private void clearExpiredRequests() {
        final Set<MessageWaitingRequest> expired =
                requests.stream().filter(MessageWaitingRequest::expired).collect(Collectors.toSet());
        requests.removeAll(expired);
    }

    @EqualsAndHashCode(of = {"userId", "chatId"})
    private static class MessageWaitingRequest {
        private final Integer userId;
        private final Long chatId;
        private final Runnable onTimeout;
        private LocalDateTime created = LocalDateTime.now();

        private MessageWaitingRequest(Message msg, Runnable onTimeout) {
            this.userId = msg.getFrom().getId();
            this.chatId = msg.getChat().getId();
            this.onTimeout = onTimeout;
        }

        private MessageWaitingRequest(Message msg) {
            this(msg, null);
        }

        private boolean expired() {
            return created.plusMinutes(1).isBefore(LocalDateTime.now());
        }
    }
}
