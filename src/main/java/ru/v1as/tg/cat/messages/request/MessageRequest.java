package ru.v1as.tg.cat.messages.request;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Consumer;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@Setter
@Accessors(fluent = true)
public class MessageRequest {
    private final Integer userId;
    private final Long chatId;
    private final LocalDateTime created;

    private @NonNull Duration timeout;
    private @NonNull Predicate<Message> filter;
    private @NonNull Runnable onTimeout;
    private @NonNull Consumer<Message> onResponse;

    public MessageRequest(Long chatId, Integer userId) {
        this.userId = userId;
        this.chatId = chatId;
        this.created = LocalDateTime.now();
        this.timeout = Duration.ofMinutes(1);
        this.filter = m -> true;
        this.onTimeout = () -> {};
    }

    public MessageRequest(Message msg) {
        this(msg.getChat().getId(), msg.getFrom().getId());
    }

    public boolean isExpired() {
        return created.plus(timeout).isBefore(LocalDateTime.now());
    }

    public MessageRequestKey getHashKey() {
        return new MessageRequestKey(this);
    }
}
