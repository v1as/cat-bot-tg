package ru.v1as.tg.cat.model;

import static ru.v1as.tg.cat.model.TgRequestPoll.State.CANCELED;
import static ru.v1as.tg.cat.model.TgRequestPoll.State.CLOSED;
import static ru.v1as.tg.cat.model.TgRequestPoll.State.OPENED;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class TgRequestPoll<T> {

    protected final Long chatId;
    protected final LocalDateTime created = LocalDateTime.now();
    protected Integer messageId;
    protected State state;
    protected T result;

    public void cancel() {
        if (!state.equals(OPENED)) {
            throw new IllegalStateException("This request is already closed");
        }
        this.state = CANCELED;
    }

    public void close(T result) {
        if (!state.equals(OPENED)) {
            throw new IllegalStateException("This request is already closed");
        }
        this.state = CLOSED;
        this.result = result;
    }

    public boolean isClosed() {
        return CLOSED.equals(state);
    }

    public Integer getMessageId() {
        return messageId;
    }

    public void setMessageId(Integer messageId) {
        if (this.messageId != null) {
            throw new IllegalStateException("Vote message is already set");
        }
        this.messageId = messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Duration getAge() {
        return Duration.between(created, LocalDateTime.now());
    }

    public boolean isOpen() {
        return OPENED.equals(state);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TgRequestPoll<?> that = (TgRequestPoll<?>) o;
        return Objects.equals(chatId, that.chatId)
                && Objects.equals(created, that.created)
                && Objects.equals(messageId, that.messageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chatId, created, messageId);
    }

    protected enum State {
        OPENED,
        CANCELED,
        CLOSED
    }
}
