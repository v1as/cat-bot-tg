package ru.v1as.tg.cat.commands.impl;

import static java.time.temporal.ChronoUnit.MINUTES;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.commands.ArgumentCallbackCommand.CallbackCommandContext;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;

@Component
@RequiredArgsConstructor
public class JoinCatFollowPhase extends AbstractPhase<JoinCatFollowPhase.Context> {

    private final CatBotData data;
    private final ScoreData scoreData;
    private final StartCommand startCommand;
    private final RedStonePhase redStonePhase;

    @Override
    protected void open() {
        PollChoice followTheCat = PollChoice.startCommandUrl("Пойти за котом");
        Context ctx = getPhaseContext();

        poll("Любопытный кот гуляет рядом")
                .removeOnChoice(true)
                .choice(EmojiConst.CAT + " Кот!", this::sayCat)
                .choice(followTheCat)
                .onSend(msg -> ctx.message = msg)
                .timeout(
                        new PollTimeoutConfiguration(Duration.of(3, MINUTES))
                                .onTimeout(this::close))
                .send();
        startCommand.register(followTheCat.getUuid(), contextWrap(this::goToCat));

        onClose(() -> startCommand.drop(followTheCat.getUuid()));
    }

    private void sayCat(ChooseContext ctx) {
        UserData user = data.getUserData(ctx.getUser());
        saveCatRequest(ctx);
        sendMessage("Любопытный кот убежал к " + user.getUsernameOrFullName());
        close();
    }

    private void goToCat(CallbackCommandContext data) {
        Context ctx = getPhaseContext();
        redStonePhase.open(data.getChat(), ctx.getChat(), ctx.message);
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

    public Context buildContext(Chat chat) {
        return new Context(chat);
    }

    public class Context extends PhaseContext {
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
