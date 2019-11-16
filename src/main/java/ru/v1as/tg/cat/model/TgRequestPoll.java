package ru.v1as.tg.cat.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TgRequestPoll<T> {

    protected final Long chatId;
    protected final LocalDateTime created = LocalDateTime.now();
    protected Integer messageId;
    protected boolean finished = false;
    protected boolean canceled = false;
    protected T result;

    public void cancel() {
        if (finished) {
            throw new IllegalStateException("This request is already closed");
        }
        this.canceled = true;
        this.finished = true;
    }

    public void finish(T result) {
        if (finished) {
            throw new IllegalStateException("This request is already closed");
        }
        this.finished = true;
        this.result = result;
    }

    public void setMessageId(Integer messageId) {
        if (this.messageId != null) {
            throw new IllegalStateException("Vote message is already set");
        }
        this.messageId = messageId;
    }

    public Integer getMessageId() {
        return messageId;
    }

    public Long getChatId() {
        return chatId;
    }

    public Duration getAge() {
        return Duration.between(created, LocalDateTime.now());
    }

    public boolean isOpen() {
        return !canceled && !finished;
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
}
