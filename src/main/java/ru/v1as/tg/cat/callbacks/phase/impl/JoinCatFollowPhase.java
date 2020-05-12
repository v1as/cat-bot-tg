package ru.v1as.tg.cat.callbacks.phase.impl;

import static java.time.LocalDateTime.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static ru.v1as.tg.cat.EmojiConst.CAT;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase.Context;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.callbacks.phase.poll.UpdateWithChoiceTextBuilder;
import ru.v1as.tg.cat.commands.ArgumentCallbackCommand.CallbackCommandContext;
import ru.v1as.tg.cat.commands.impl.StartCommand;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.service.CatEventService;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinCatFollowPhase extends AbstractPhase<Context> {

    private static final ImmutableList<String> YOU_ARE_LATE_MESSAGE =
            ImmutableList.of(
                    "Прости, но, похоже, тебя опередили.",
                    "Сожалею, но кто-то оказался быстрее тебя.");
    private final CatUserEventDao catEventDao;
    private final StartCommand startCommand;
    private final CatEventService catEventService;
    private final CuriosCatQuestProducer curiosCatQuestProducer;
    private final BotConfiguration conf;

    @Override
    protected void open() {
        PollChoice followTheCat = PollChoice.startCommandUrl(conf.getBotName(), "Пойти за котом");

        PollTimeoutConfiguration removeAfter5Min =
                new PollTimeoutConfiguration(Duration.of(5, MINUTES))
                        .removeMsg(true)
                        .onTimeout(this::close);
        Context ctx = getPhaseContext();
        poll("Любопытный Кот гуляет рядом")
                .closeOnChoose(false)
                .closeTextBuilder(new UpdateWithChoiceTextBuilder())
                .removeOnClose(true)
                .choice(CAT + " Кот!", this::scheduleSayCat)
                .choice(followTheCat)
                .onSend(msg -> ctx.message = msg)
                .timeout(removeAfter5Min)
                .send();
        ctx.startCommandArgument = followTheCat.getUuid();
        startCommand.register(ctx.startCommandArgument, contextWrap(this::checkUserPriority));
    }

    @Override
    public void close() {
        super.close();
        Context ctx = getPhaseContext();
        startCommand.drop(ctx.startCommandArgument);
        startCommand.register(ctx.startCommandArgument, contextWrap(this::youAreLate));
        botClock.schedule(() -> startCommand.drop(ctx.startCommandArgument), 1, TimeUnit.MINUTES);
    }

    private void scheduleSayCat(ChooseContext choice) {
        Context ctx = getPhaseContext();
        if (!ctx.hasLazyCandidate) {
            ctx.hasLazyCandidate = true;
        } else {
            return;
        }

        log.info(
                "Waiting for 10 seconds before close request for user '{}'",
                choice.getUser().getUsernameOrFullName());
        String text =
                String.format(
                        "Похоже, %s не пойдёт за Любопытным Котом, может кто-то другой сможет?",
                        choice.getUser().getUsernameOrFullName());
        editMessageText(ctx.message, text);
        botClock.schedule(contextWrap(() -> sayCat(choice)), 10, TimeUnit.SECONDS);
    }

    private void sayCat(ChooseContext ctx) {
        close();
        String user = ctx.getUser().getUsernameOrFullName();
        log.info("Trying to say cat for user '{}'", user);
        final CatRequestVote cats = saveCatRequest(ctx);
        message(cats.getMessage(user));
    }

    private void checkUserPriority(CallbackCommandContext data) {
        int recentlyCatsAmount =
                catEventDao.findByUserIdAndDateGreaterThan(data.getUserId(), now().minusHours(8))
                        .stream()
                        .filter(CatUserEvent::isNotReal)
                        .map(CatUserEvent::getResult)
                        .mapToInt(CatRequestVote::getAmount)
                        .sum();

        if (recentlyCatsAmount == 0) {
            goToCat(data);
        } else {
            final int odds = Math.min(2_000 + recentlyCatsAmount * 500, 10_000);
            log.info("Waiting user odds {} ms", odds);
            botClock.schedule(
                    contextWrap(
                            () -> {
                                log.info("Odds are finished.");
                                goToCat(data);
                            }),
                    odds,
                    MILLISECONDS);
        }
    }

    private void goToCat(CallbackCommandContext data) {
        Context ctx = getPhaseContext();
        if (ctx.isClosed()) {
            youAreLate(data);
        } else {
            close();
            final TgChat chat = getPhaseContext().getChat();
            AbstractCuriosCatPhase nextPhase = curiosCatQuestProducer.get(data.getUser(), chat);
            nextPhase.open(data.getChat(), ctx.getChat(), data.getUser(), ctx.message);
        }
    }

    private void youAreLate(CallbackCommandContext data) {
        message(data.getChat(), random(YOU_ARE_LATE_MESSAGE));
    }

    private CatRequestVote saveCatRequest(ChooseContext choice) {
        final TgUser user = choice.getUser();
        final TgChat chat = getPhaseContext().getChat();
        final Message message = getPhaseContext().message;
        return catEventService.saveCuriosCat(user, chat, message.getMessageId());
    }

    public void open(TgChat chat) {
        this.open(new Context(chat));
    }

    static class Context extends PhaseContext {

        private String startCommandArgument;
        private Message message;
        private boolean hasLazyCandidate = false;

        private Context(TgChat chat) {
            super(chat);
        }

        @Override
        public void close() {
            super.close();
        }
    }
}
