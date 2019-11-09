package ru.v1as.tg.cat.callbacks.phase;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;

@RequiredArgsConstructor
@Slf4j
public class PhaseContext {

    private final Chat chat;
    private final User user;
    private List<SimplePoll> polls = new ArrayList<>();
    private boolean finished = false;

    public void checkNotClose() {
        if (finished) {
            log.info("Phase context is already closed");
            throw new PhaseContextClosedException("This phase context is already closed.");
        }
    }

    protected SimplePoll poll(String text) {
        checkNotClose();
        SimplePoll simplePoll = new SimplePoll();
        polls.add(simplePoll);
        return simplePoll.chatId(chat.getId()).text(text);
    }

    public void close() {
        checkNotClose();
        this.finished = true;
        for (SimplePoll poll : polls) {
            try {
                poll.close();
            } catch (Exception e) {
                log.error("Error while closing poll");
            }
        }
        polls.clear();
    }

    public Long getChatId() {
        return chat != null ? chat.getId() : null;
    }

    public Chat getChat() {
        return chat;
    }

    public User getUser() {
        return user;
    }
}
