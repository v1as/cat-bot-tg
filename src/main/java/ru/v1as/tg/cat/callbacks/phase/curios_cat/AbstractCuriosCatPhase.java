package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.EmojiConst.DIE;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.DIE_AMULET;
import static ru.v1as.tg.cat.utils.TimeoutUtils.getMsForTextReading;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import lombok.Getter;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPublicChatPhase;
import ru.v1as.tg.cat.callbacks.phase.PersonalPublicChatPhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
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
        final CuriosCatContext ctx = getPhaseContext();
        final Integer userId = ctx.getUser().getId();
        final int amuletCharges =
                paramResource.paramInt(ctx.getPublicChatId(), ctx.getUser().getId(), DIE_AMULET);
        ctx.dieAmulet = amuletCharges > 0;
        this.catBotData.incrementPhase(userId);
    }

    @Override
    protected void beforeClose() {
        checkDieCharges();
        catBotData.decrementPhase(getPhaseContext().getUser().getId());
    }

    private void checkDieCharges() {
        final CuriosCatContext ctx = getPhaseContext();
        if (ctx.dieAmulet) {
            final Integer userId = ctx.getUser().getId();
            final Boolean lastCharge =
                    paramResource.increment(ctx.getPublicChatId(), userId, DIE_AMULET, -1).stream()
                            .findFirst()
                            .map(e -> e.getOldValue().equals("1"))
                            .orElse(false);
            if (lastCharge) {
                message(
                        "Кристаллические кости рассыпались у вас в руках, исчерпав свою магическую силу "
                                + DIE);
            }
        }
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
        super.message(addDieText(text));
        botClock.wait(getMsForTextReading(text.length()));
    }

    private String addDieText(String text) {
        final CuriosCatContext phaseContext = getPhaseContext();
        if (phaseContext.dieAmulet && phaseContext.randomFlag()) {
            text = DIE + " " + text;
            phaseContext.randomFlag(false);
        }
        return text;
    }

    @Override
    protected void message(TgChat chat, String text) {
        super.message(chat, text);
        botClock.wait(getMsForTextReading(text.length()));
    }

    protected void catchUpCatAndClose(@NonNull CatRequestVote result) {
        CuriosCatContext ctx = getPhaseContext();
        final TgUser user = ctx.getUser();
        final TgChat publicChat = ctx.getPublicChat();
        result =
                catEventService.saveCuriosCatQuest(
                        user, publicChat, ctx.message, result, getName());
        message(publicChat, result.getMessage(user.getUsernameOrFullName()));
        close();
    }

    public String getName() {
        return getClass().getSimpleName();
    }

    public boolean filter(TgUser user, TgChat chat) {
        return true;
    }

    @Getter
    public static class CuriosCatContext extends PersonalPublicChatPhaseContext {


        private final Message message;
        private final Map<String, Object> values = new HashMap<>();
        private boolean dieAmulet = false;

        CuriosCatContext(TgChat chat, TgChat publicChat, TgUser user, Message message) {
            super(chat, user, publicChat);
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

    }
}
