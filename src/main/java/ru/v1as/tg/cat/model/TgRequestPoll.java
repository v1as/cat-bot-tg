package ru.v1as.tg.cat.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;

@Getter
@RequiredArgsConstructor
public class TgRequestPoll<T> {

    protected final TgChat chat;
    protected final LocalDateTime created = LocalDateTime.now();
    boolean finished = false;
    boolean canceled = false;
    protected T result;
    protected Message voteMessage;

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

    public void setVoteMessage(Message voteMessage) {
        if (this.voteMessage != null) {
            throw new IllegalStateException("Vote message is already set");
        }
        this.voteMessage = voteMessage;
    }

    public Message getVoteMessage() {
        return voteMessage;
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
        return Objects.equals(chat, that.chat)
                && Objects.equals(created, that.created)
                && Objects.equals(voteMessage, that.voteMessage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chat, created, voteMessage);
    }
}
