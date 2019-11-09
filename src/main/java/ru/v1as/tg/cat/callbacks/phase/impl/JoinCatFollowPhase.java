package ru.v1as.tg.cat.callbacks.phase.impl;

import static java.time.temporal.ChronoUnit.MINUTES;
import static ru.v1as.tg.cat.model.UpdateUtils.getUsernameOrFullName;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import com.google.common.collect.ImmutableList;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
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
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.utils.RandomChoice;
import ru.v1as.tg.cat.utils.RandomNoRepeats;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinCatFollowPhase extends AbstractPhase<Context> {

    private static final ImmutableList<String> YOU_ARE_LATE_MESSAGE =
            ImmutableList.of(
                    "Прости, но, похоже, тебя опередили.",
                    "Сожалею, но кто-то оказался быстрее тебя.");

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private final CatBotData data;
    private final ScoreData scoreData;
    private final StartCommand startCommand;
    private final List<AbstractCuriosCatPhase> nextPhases;
    private RandomChoice<AbstractCuriosCatPhase> nextPhaseChoice;

    @PostConstruct
    public void init() {
        nextPhaseChoice = new RandomNoRepeats<>(nextPhases);
        log.info(
                "Curios cat quests amount: '{}' and values: {}",
                nextPhases.size(),
                nextPhases.stream()
                        .map(Object::getClass)
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining("; ", "[", "]")));
    }

    @Override
    protected void open() {
        PollChoice followTheCat = PollChoice.startCommandUrl("Пойти за котом");

        PollTimeoutConfiguration removeAfter5Min =
                new PollTimeoutConfiguration(Duration.of(5, MINUTES))
                        .removeMsg(true)
                        .onTimeout(this::close);
        Context ctx = getPhaseContext();
        poll("Любопытный кот гуляет рядом")
                .closeOnChoose(false)
                .closeTextBuilder(new UpdateWithChoiceTextBuilder())
                .removeOnClose(true)
                .choice(EmojiConst.CAT + " Кот!", this::scheduleSayCat)
                .choice(followTheCat)
                .onSend(msg -> ctx.message = msg)
                .timeout(removeAfter5Min)
                .send();
        ctx.startCommandArgument = followTheCat.getUuid();
        startCommand.register(ctx.startCommandArgument, contextWrap(this::goToCat));
    }

    @Override
    public void close() {
        super.close();
        Context ctx = getPhaseContext();
        startCommand.drop(ctx.startCommandArgument);
        startCommand.register(ctx.startCommandArgument, contextWrap(this::youAreLate));
        executorService.schedule(
                () -> startCommand.drop(ctx.startCommandArgument), 1, TimeUnit.MINUTES);
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
                getUsernameOrFullName(choice.getUser()));
        String text =
                String.format(
                        "Похоже, %s не пойдёт за Любопытным Котом, может кто-то другой сможет?",
                        getUsernameOrFullName(choice.getUser()));
        editMessageText(ctx.message, text);
        executorService.schedule(contextWrap(() -> sayCat(choice)), 10, TimeUnit.SECONDS);
    }

    private void sayCat(ChooseContext ctx) {
        String user = getUsernameOrFullName(ctx.getUser());
        log.info("Trying to sat cat for user '{}'", user);
        getPhaseContext().checkNotClose();
        saveCatRequest(ctx);
        message("Любопытный кот убежал к " + user);
        close();
    }

    private void goToCat(CallbackCommandContext data) {
        close();
        Context ctx = getPhaseContext();
        AbstractCuriosCatPhase nextPhase = nextPhaseChoice.get();
        nextPhase.open(data.getChat(), ctx.getChat(), data.getUser(), ctx.message);
    }

    private void youAreLate(CallbackCommandContext data) {
        final String msg = random(YOU_ARE_LATE_MESSAGE);
        message(data.getChat(), msg);
    }

    private void saveCatRequest(ChooseContext choice) {
        Context phaseContext = getPhaseContext();
        UserData userData = data.getUserData(choice.getUser());
        CatChatData chatData = data.getChatData(choice.getChat().getId());
        CatRequest catRequest = new CatRequest(phaseContext.message, userData, chatData);
        catRequest.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest);
    }

    public void open(Chat chat, User user) {
        this.open(new Context(chat, user));
    }

    static class Context extends PhaseContext {

        private String startCommandArgument;
        private Message message;
        private boolean hasLazyCandidate = false;

        private Context(Chat chat, User user) {
            super(chat, user);
        }

        @Override
        public void close() {
            super.close();
        }
    }
}
