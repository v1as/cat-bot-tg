package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.service.CatEventService.CAT_REWARD;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.CatEventService;
import ru.v1as.tg.cat.service.clock.BotClock;

public abstract class AbstractCuriosCatPhase extends AbstractPhase<CuriosCatContext> {

    @Autowired protected CatBotData data;
    @Autowired protected CatEventService catEventService;
    @Autowired protected BotClock botClock;
    protected final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .onTimeout(() -> this.catchUpCatAndClose(NOT_CAT));

    public CuriosCatContext buildContext(
            TgChat chat, TgChat publicChat, TgUser user, Message message) {
        return new CuriosCatContext(chat, publicChat, user, message);
    }

    public void open(TgChat chat, TgChat publicChat, TgUser user, Message curiosCatMsg) {
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
    protected void message(TgUser user, String text) {
        super.message(user, text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    @Override
    protected void message(String text) {
        super.message(text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    @Override
    protected void message(TgChat chat, String text) {
        super.message(chat, text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    protected void catchUpCatAndClose(CatRequestVote result) {
        CuriosCatContext ctx = getPhaseContext();
        final TgUser user = ctx.getUser();
        final TgChat publicChat = ctx.getPublicChat();
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
        String reward =
                result.getAmount() > 0
                        ? "(+" + result.getAmount() * CAT_REWARD.intValue() + MONEY_BAG + ")"
                        : "";
        message(publicChat, message + user.getUsernameOrFullName());
        catEventService.saveCuriosCatQuest(
                user, publicChat, ctx.message, result, getClass().getSimpleName());
        close();
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    @Getter
    class CuriosCatContext extends PhaseContext {

        private final TgUser user;
        private final Message message;
        private final TgChat publicChat;
        private final Map<String, Object> values = new HashMap<>();

        CuriosCatContext(TgChat chat, TgChat publicChat, TgUser user, Message message) {
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

        public TgUser getUser() {
            return user;
        }
    }
}
