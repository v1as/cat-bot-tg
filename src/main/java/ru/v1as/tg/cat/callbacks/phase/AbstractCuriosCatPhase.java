package ru.v1as.tg.cat.callbacks.phase;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;

public abstract class AbstractCuriosCatPhase extends AbstractPhase<CuriosCatContext> {

    protected static final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .message("Любопытный кот убежал");

    @Autowired protected CatBotData data;
    @Autowired protected ScoreData scoreData;

    public CuriosCatContext buildContext(Chat chat, Chat publicChat, Message message) {
        return new CuriosCatContext(chat, publicChat, message);
    }

    public void open(Chat chat, Chat publicChat, Message curiosCatMsg) {
        CuriosCatContext curiosCatContext = buildContext(chat, publicChat, curiosCatMsg);
        this.open(curiosCatContext);
    }

    @Override
    protected SimplePoll poll(String text) {
        return super.poll(text).timeout(TIMEOUT_LEAVE_CAT);
    }

    protected void catchUpCatAndClose(ChooseContext choice, CatRequestVote result) {
        CuriosCatContext ctx = getPhaseContext();
        CatChatData chat = data.getChatData(ctx.getPublicChat().getId());
        UserData user = data.getUserData(choice.getUser());
        String message = "";
        if (result == CatRequestVote.CAT1) {
            message = "Любопытный кот убегает к ";
        } else if (result == CatRequestVote.CAT2) {
            message = "Вот это удача! Целых два кота засчитано игроку ";
        } else if (result == CatRequestVote.CAT3) {
            message = "Так просто не бывает... Целых ТРИ кота засчитано игроку ";
        } else if (result == CatRequestVote.NOT_CAT) {
            message = "Любопытный кот сбегает от игрока ";
        }
        message(ctx.getPublicChat(), message + user.getUsernameOrFullName());
        CatRequest catRequest = new CatRequest(ctx.message, user, chat);
        catRequest.finish(result);
        scoreData.save(catRequest);
        close();
    }

    @Getter
    class CuriosCatContext extends PhaseContext {

        private Message message;
        private Chat publicChat;
        private Map<String, Object> values = new HashMap<>();

        CuriosCatContext(Chat chat, Chat publicChat, Message message) {
            super(chat);
            this.message = message;
            this.publicChat = publicChat;
        }

        public void set(String name, Object value) {
            checkNotClose();
            values.put(name, value);
        }

        public <T> T get(String name) {
            return get(name, null);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(String name, T defaultValue) {
            return (T) values.computeIfAbsent(name, n -> defaultValue);
        }

        public Integer increment(String name) {
            checkNotClose();
            Integer val = (Integer) values.computeIfAbsent(name, n -> 0);
            set(name, ++val);
            return val;
        }
    }
}
