package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT4;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.service.CatEventService.CAT_REWARD;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
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
import ru.v1as.tg.cat.model.random.RandomRequest;
import ru.v1as.tg.cat.service.CatEventService;

public abstract class AbstractCuriosCatPhase extends AbstractPublicChatPhase<CuriosCatContext> {

    protected static final RandomRequest<CatRequestVote> RANDOM_REQUEST_CAT_1_2_3 =
            new RandomRequest<CatRequestVote>().add(CAT1, 60).add(CAT2, 30).add(CAT3, 10);

    @Autowired protected CatEventService catEventService;

    protected final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(30, SECONDS))
                    .removeMsg(true)
                    .onTimeout(() -> this.catchUpCatAndClose(NOT_CAT));

    public void open(TgChat chat, TgChat publicChat, TgUser user, Message curiosCatMsg) {
        this.open(new CuriosCatContext(chat, publicChat, user, curiosCatMsg));
    }

    @Override
    protected void beforeOpen() {
        final Integer userId = getPhaseContext().getUser().getId();
        this.catBotData.incrementPhase(userId);
    }

    @Override
    protected void beforeClose() {
        final Integer userId = getPhaseContext().getUser().getId();
        this.catBotData.decrementPhase(userId);
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
        result =
                catEventService.saveCuriosCatQuest(
                        user, publicChat, ctx.message, result, getName());
        String message = "";
        if (result == NOT_CAT) {
            message = "Любопытный кот сбегает от игрока ";
        } else if (result == CAT1) {
            message = "Любопытный кот убегает к ";
        } else if (result == CAT2) {
            message = "Два кота засчитано игроку ";
        } else if (result == CAT3) {
            message = "Целых три кота засчитано игроку ";
        } else if (result == CAT4) {
            message = "Целых 4 кота засчитано игроку ";
        }
        String reward =
                result.getAmount() > 0
                        ? " (+" + result.getAmount() * CAT_REWARD + MONEY_BAG + ")"
                        : "";
        message(publicChat, message + user.getUsernameOrFullName() + reward);
        close();
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public boolean filter(TgUser user, TgChat chat) {
        return true;
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
