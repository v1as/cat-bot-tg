package ru.v1as.tg.cat.messages;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.messages.TgMessageProcessor.InterruptMessageProcessing;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public abstract class RequestWithTimeoutCommandHandler implements MessageHandler {

    private final Set<LoadDumpRequest> requests = new HashSet<>();

    public void addRequest(Message msg, Runnable timeout) {
        requests.add(new LoadDumpRequest(msg, timeout));
    }

    @Override
    public final void handle(Message message, TgChat chat, TgUser user) {
        clearExpiredRequests();
        final LoadDumpRequest request = new LoadDumpRequest(message);
        if (!requests.contains(request)) {
            return;
        }
        if (this.handleRequest(message, chat, user)) {
            requests.remove(request);
            throw new InterruptMessageProcessing();
        }
    }

    @Override
    public int priority() {
        return TOP_PRIORITY;
    }

    protected abstract boolean handleRequest(Message message, TgChat chat, TgUser user);

    private void clearExpiredRequests() {
        final Set<LoadDumpRequest> expired =
                requests.stream().filter(LoadDumpRequest::expired).collect(Collectors.toSet());
        requests.removeAll(expired);
    }

    @EqualsAndHashCode(of = {"userId", "chatId"})
    private static class LoadDumpRequest {
        private final Integer userId;
        private final Long chatId;
        private final Runnable onTimeout;
        private LocalDateTime created = LocalDateTime.now();

        private LoadDumpRequest(Message msg, Runnable onTimeout) {
            this.userId = msg.getFrom().getId();
            this.chatId = msg.getChat().getId();
            this.onTimeout = onTimeout;
        }

        private LoadDumpRequest(Message msg) {
            this(msg, null);
        }

        private boolean expired() {
            return created.plusMinutes(1).isBefore(LocalDateTime.now());
        }
    }
}