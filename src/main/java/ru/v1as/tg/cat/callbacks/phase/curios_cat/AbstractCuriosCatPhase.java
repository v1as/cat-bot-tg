package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
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

    protected static final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .message("Любопытный кот убежал");

    @Autowired protected CatBotData data;
    @Autowired protected ScoreData scoreData;
    @Autowired protected BotClock botClock;

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
