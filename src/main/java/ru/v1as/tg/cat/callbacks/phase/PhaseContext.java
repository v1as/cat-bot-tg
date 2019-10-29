package ru.v1as.tg.cat.callbacks.phase;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;

@RequiredArgsConstructor
@Slf4j
public class PhaseContext {

    private final Chat chat;
    private List<SimplePoll> polls = new ArrayList<>();
    private List<Runnable> onCloses = new ArrayList<>();

    public void onClose(Runnable onClose) {
        onCloses.add(onClose);
    }

    protected SimplePoll poll(String text) {
        SimplePoll simplePoll = new SimplePoll();
        polls.add(simplePoll);
        return simplePoll.chatId(chat.getId()).text(text);
    }

    public void close() {
        for (SimplePoll poll : polls) {
            try {
                poll.close();
            } catch (Exception e) {
                log.error("Error while closing poll");
            }
        }
        for (Runnable onClose : onCloses) {
            try {
                onClose.run();
            } catch (Exception e) {
                log.error("Error while closing poll");
            }
        }
        onCloses.clear();
        polls.clear();
    }

    public Long getChatId() {
        return chat != null ? chat.getId() : null;
    }

    public Chat getChat() {
        return chat;
    }
}
