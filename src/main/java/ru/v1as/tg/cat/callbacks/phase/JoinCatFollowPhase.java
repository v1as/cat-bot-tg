package ru.v1as.tg.cat.callbacks.phase;

import static java.time.temporal.ChronoUnit.MINUTES;
import static ru.v1as.tg.cat.model.UpdateUtils.getUsernameOrFullName;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.NopeCloseTextBuilder;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.commands.ArgumentCallbackCommand.CallbackCommandContext;
import ru.v1as.tg.cat.commands.impl.StartCommand;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;

@Slf4j
@Component
@RequiredArgsConstructor
public class JoinCatFollowPhase extends AbstractPhase<JoinCatFollowPhase.Context> {

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private final CatBotData data;
    private final ScoreData scoreData;
    private final StartCommand startCommand;
    private final List<AbstractCuriosCatPhase> nextPhases;

    @Override
    protected void open() {
        PollChoice followTheCat = PollChoice.startCommandUrl("Пойти за котом");
        Context ctx = getPhaseContext();

        PollTimeoutConfiguration removeAfter5Min =
                new PollTimeoutConfiguration(Duration.of(5, MINUTES))
                        .removeMsg(true)
                        .onTimeout(contextWrap(this::close));

        poll("Любопытный кот гуляет рядом")
                .closeOnChoose(false)
                .removeOnClose(true)
                .closeTextBuilder(new NopeCloseTextBuilder())
                .choice(EmojiConst.CAT + " Кот!", contextWrap(this::scheduleSayCat))
                .choice(followTheCat)
                .onSend(msg -> ctx.message = msg)
                .timeout(removeAfter5Min)
                .send();
        startCommand.register(followTheCat.getUuid(), contextWrap(this::goToCat));

        onClose(() -> startCommand.drop(followTheCat.getUuid()));
    }

    private void scheduleSayCat(ChooseContext ctx) {
        log.info(
                "Waiting for 10 seconds before close request for user '{}'",
                getUsernameOrFullName(ctx.getUser()));
        executorService.schedule(contextWrap(() -> sayCat(ctx)), 10, TimeUnit.SECONDS);
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
        Context ctx = getPhaseContext();
        AbstractCuriosCatPhase nexPhase = random(nextPhases);
        nexPhase.open(data.getChat(), ctx.getChat(), ctx.message);
        close();
    }

    private void saveCatRequest(ChooseContext choice) {
        Context phaseContext = getPhaseContext();
        UserData userData = data.getUserData(choice.getUser());
        CatChatData chatData = data.getChatData(choice.getChat().getId());
        CatRequest catRequest = new CatRequest(phaseContext.message, userData, chatData);
        catRequest.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest);
    }

    public void open(Chat chat) {
        this.open(new Context(chat));
    }

    class Context extends PhaseContext {
        private Message message;

        public Context(Chat chat) {
            super(chat);
        }

        @Override
        public void close() {
            super.close();
        }
    }
}
