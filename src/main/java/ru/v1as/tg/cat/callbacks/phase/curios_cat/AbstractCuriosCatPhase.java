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
import ru.v1as.tg.cat.callbacks.phase.AbstractPublicChatPhase;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.PublicChatPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.PhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.callbacks.phase.poll.interceptor.TimeoutPhaseContextChoiceAroundInterceptor;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.CatEventService;

public abstract class AbstractCuriosCatPhase extends AbstractPublicChatPhase<CuriosCatContext> {

    @Autowired protected CatBotData data;
    @Autowired protected CatEventService catEventService;

    protected final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .onTimeout(() -> this.catchUpCatAndClose(NOT_CAT));

    public void open(TgChat chat, TgChat publicChat, TgUser user, Message curiosCatMsg) {
        this.open(new CuriosCatContext(chat, publicChat, user, curiosCatMsg));
    }

    @Override
    protected TgInlinePoll poll(String text) {
        return super.poll(text).timeout(TIMEOUT_LEAVE_CAT);
    }

    @Override
    protected PhaseContextChoiceAroundInterceptor<CuriosCatContext> getChoiceAroundInterceptor(
            TgInlinePoll poll, ThreadLocal<CuriosCatContext> phaseContext) {
        int pollTextLength = getTextLength(poll);
        int afterPollTimeoutMs = getMsForTextReading(pollTextLength);
        return new TimeoutPhaseContextChoiceAroundInterceptor<>(
                phaseContext, botClock, afterPollTimeoutMs);
    }

    protected int getTextLength(TgInlinePoll poll) {
        int messageLen = poll.text().length();
        int choicesLen =
                poll.getChoices().stream().map(PollChoice::getText).mapToInt(String::length).sum();
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
                        ? " (+" + result.getAmount() * CAT_REWARD.intValue() + MONEY_BAG + ")"
                        : "";
        message(publicChat, message + user.getUsernameOrFullName() + reward);
        catEventService.saveCuriosCatQuest(
                user, publicChat, ctx.message, result, getClass().getSimpleName());
        close();
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    @Getter
    public static class CuriosCatContext extends PublicChatPhaseContext {

        private final TgUser user;
        private final Message message;
        private final Map<String, Object> values = new HashMap<>();

        CuriosCatContext(TgChat chat, TgChat publicChat, TgUser user, Message message) {
            super(chat, publicChat);
            this.user = user;
            this.message = message;
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
