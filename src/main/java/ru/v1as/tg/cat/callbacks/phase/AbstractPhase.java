package ru.v1as.tg.cat.callbacks.phase;

import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.poll.AbstractPoll;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.tg.KeyboardUtils;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public abstract class AbstractPhase implements Phase {

    protected final UnsafeAbsSender sender;
    private final TgCallbackProcessor callbackProcessor;

    protected final ChatData chat;

    protected List<AbstractPoll> polls = new ArrayList<>();

    protected void sendMessage(UserData userData, String text) {
        sender.executeUnsafe(new SendMessage(userData.getChatId(), text));
    }

    protected void deleteMsg(Message curiosCatMessage) {
        sender.executeUnsafe(KeyboardUtils.deleteMsg(curiosCatMessage));
    }

    @SneakyThrows
    protected void timeoutSeconds(int i) {
        try {
            Thread.sleep(i * 1000);
        } catch (InterruptedException e) {
            close();
            throw e;
        }
    }

    protected SimplePoll poll(String text) {
        return new SimplePoll(sender, callbackProcessor).chatId(chat.getChatId()).text(text);
    }

    protected SimplePoll privatePoll(UserData userData, String text) {
        return new SimplePoll(sender, callbackProcessor).chatId(userData.getChatId()).text(text);
    }

    protected void sendMessage(String text) {
        sender.executeUnsafe(new SendMessage(chat.getChatId(), text));
    }

    @Override
    public void close() {
        for (AbstractPoll poll : polls) {
            try {
                poll.close();
            } catch (Exception e) {
                log.error("Erorr while closing poll");
            }
        }
    }

}
