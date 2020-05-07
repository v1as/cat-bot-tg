package ru.v1as.tg.cat.messages.request;

import static java.time.LocalDateTime.now;
import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.BREAK;
import static ru.v1as.tg.cat.messages.MessageHandler.MessageHandlerResult.SKIPPED;
import static ru.v1as.tg.cat.utils.LogUtils.logExceptions;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.messages.MessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public abstract class RequestWithTimeoutCommandHandler<T> implements MessageHandler {

    private final Set<MessageWaitingRequest<T>> requests = new HashSet<>();

    public MessageWaitingRequest<T> addRequest(Long chatId, Integer userId, T data) {
        final MessageWaitingRequest<T> request = new MessageWaitingRequest<>(chatId, userId, data);
        requests.add(request);
        return request;
    }

    public MessageWaitingRequest<T> addRequest(Message msg, T data) {
        return addRequest(msg.getChatId(), msg.getFrom().getId(), data);
    }

    @Override
    public final MessageHandlerResult handle(Message msg, TgChat chat, TgUser user) {
        clearExpiredRequests();
        final MessageWaitingRequest<T> request =
                new MessageWaitingRequest<>(msg.getChatId(), msg.getFrom().getId(), null);
        if (!requests.contains(request)) {
            return SKIPPED;
        }
        if (this.handleRequest(msg, chat, user, request.data)) {
            requests.remove(request);
            return BREAK;
        }
        return SKIPPED;
    }

    @Override
    public int priority() {
        return TOP_PRIORITY;
    }

    protected abstract boolean handleRequest(Message message, TgChat chat, TgUser user, T data);

    private void clearExpiredRequests() {
        final Set<MessageWaitingRequest<T>> expired =
                requests.stream()
                        .filter(MessageWaitingRequest::expired)
                        .peek(logExceptions(MessageWaitingRequest::doOnTimeout))
                        .collect(Collectors.toSet());
        requests.removeAll(expired);
    }

    @EqualsAndHashCode(of = {"userId", "chatId"})
    protected static class MessageWaitingRequest<T> {
        private final Integer userId;
        @Getter private final Long chatId;
        private final T data;
        private final LocalDateTime created = now();
        @Setter private Consumer<T> timeout = null;
        @Setter private int minutesToLive = 1;

        private MessageWaitingRequest(Long chatId, Integer userId, T data) {
            this.userId = userId;
            this.chatId = chatId;
            this.data = data;
        }

        private boolean expired() {
            return created.plusMinutes(minutesToLive).isBefore(now());
        }

        public void doOnTimeout() {
            if (timeout != null) {
                timeout.accept(data);
            }
        }
    }
}
