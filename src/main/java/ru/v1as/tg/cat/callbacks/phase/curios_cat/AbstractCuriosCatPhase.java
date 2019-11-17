package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.callbacks.phase.poll.SimplePoll;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.PhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.TimeoutPhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.service.clock.BotClock;

public abstract class AbstractCuriosCatPhase extends AbstractPhase<CuriosCatContext> {

    protected final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .onTimeout(() -> this.catchUpCatAndClose(NOT_CAT));

    @Autowired protected CatBotData data;
    @Autowired protected ScoreData scoreData;
    @Autowired protected BotClock botClock;

    public CuriosCatContext buildContext(Chat chat, Chat publicChat, User user, Message message) {
        return new CuriosCatContext(chat, publicChat, user, message);
    }

    public void open(Chat chat, Chat publicChat, User user, Message curiosCatMsg) {
        CuriosCatContext curiosCatContext = buildContext(chat, publicChat, user, curiosCatMsg);
        this.open(curiosCatContext);
    }

    @Override
    protected SimplePoll poll(String text) {
        return super.poll(text).timeout(TIMEOUT_LEAVE_CAT);
    }

    @Override
    protected PhaseContextChoiceAroundInterceptor<CuriosCatContext> getChoiceAroundInterceptor(
            SimplePoll poll, ThreadLocal<CuriosCatContext> phaseContext) {
        int pollTextLength = getTextLength(poll);
        int afterPollTimeoutMs = getMsForTextReading(pollTextLength);
        return new TimeoutPhaseContextChoiceAroundInterceptor<>(
                phaseContext, botClock, afterPollTimeoutMs);
    }

    protected int getTextLength(SimplePoll poll) {
        int messageLen = poll.text().length();
        int choicesLen =
                poll.getChoices().values().stream()
                        .map(PollChoice::getText)
                        .mapToInt(String::length)
                        .sum();
        return messageLen + choicesLen;
    }

    @Override
    protected void message(UserData userData, String text) {
        super.message(userData, text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    @Override
    protected void message(String text) {
        super.message(text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    @Override
    protected void message(Chat chat, String text) {
        super.message(chat, text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    protected void catchUpCatAndClose(CatRequestVote result) {
        CuriosCatContext ctx = getPhaseContext();
        CatChatData chat = data.getChatData(ctx.getPublicChat().getId());
        UserData user = data.getUserData(ctx.getUser());
        String message = "";
        if (result == CatRequestVote.CAT1) {
            message = "Любопытный кот убегает к ";
        } else if (result == CatRequestVote.CAT2) {
            message = "Вот это удача! Целых два кота засчитано игроку ";
        } else if (result == CatRequestVote.CAT3) {
            message = "Так просто не бывает... Целых ТРИ кота засчитано игроку ";
        } else if (result == NOT_CAT) {
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

        private final User user;
        private final Message message;
        private final Chat publicChat;
        private final Map<String, Object> values = new HashMap<>();

        CuriosCatContext(Chat chat, Chat publicChat, User user, Message message) {
            super(chat);
            this.user = user;
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

        public User getUser() {
            return user;
        }
    }
}
