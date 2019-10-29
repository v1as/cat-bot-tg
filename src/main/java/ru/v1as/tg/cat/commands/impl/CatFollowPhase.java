package ru.v1as.tg.cat.commands.impl;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;

@Component
@RequiredArgsConstructor
public class CatFollowPhase extends AbstractPhase<CatFollowPhase.Context> {

    private static final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(10, SECONDS))
                    .removeMsg(true)
                    .message("Любопытный кот убежал");

    private final CatBotData data;
    private final ScoreData scoreData;

    @Override
    protected void open() {
        poll("Кот неторопливо бежит впереди вас")
                .choice("Попытаться догнать кота", this::fastFollowCat)
                .choice("Спокойно следовать", this::followTheCat)
                .timeout(TIMEOUT_LEAVE_CAT);
    }

    private void fastFollowCat(ChooseContext choice) {
        sendMessage(
                "Кот испугался и рванул что было сил, вам не удалось его догнать."
                        + " Хуже того, никто не слышал как вы кричали 'Кот'.");
        close();
    }

    private void followTheCat(ChooseContext data) {
        poll("Вы продолжаете осторожно следовать за котом, пристально следя за ним взглядом. "
                        + "Боковым зрением вы вдруг замечаете, как что-то блестит на дороге")
                .choice("Разглядеть находку", this::resStone)
                .choice("Не отвлекаться", this::catchCat)
                .timeout(TIMEOUT_LEAVE_CAT);
    }

    private void resStone(ChooseContext choice) {
        UserData user = data.getUserData(choice.getUser());
        sendMessage(
                "Вы остановились чтобы разглядеть находку. "
                        + "Это оказался затейливый красный камешек,"
                        + " пожалуй вы заберёте его себе - в хозяйстве всё пригодится.");
        // todo timeout
        sendMessage(
                String.format("Игрок '%s' находит красный камень", user.getUsernameOrFullName()));
        sendMessage(
                "Пока вы разглядывали драгоценность кота и след простыл,"
                        + " хотя в воздухе остался лишь след улыбки кота."
                        + " Похоже он не просто так вас сюда привёл.");
        close();
    }

    private void catchCat(ChooseContext choice) {
        UserData user = data.getUserData(choice.getUser());
        sendMessage("Кот остановился, и внимательно посмотрел на вас, похоже вы его не поняли.");
        // todo timeoutSeconds(3);
        sendMessage("Любопытный кот подбежал и потёрся о вашу ногу.");
        sendMessage(
                getPhaseContext().publicChat,
                "Любопытный кот убегает к " + user.getUsernameOrFullName());
        saveCatRequest(choice);
        close();
    }

    private void saveCatRequest(ChooseContext choice) {
        Context ctx = getPhaseContext();
        CatChatData chat = data.getChatData(choice.getChat().getId());
        UserData user = data.getUserData(choice.getUser());
        CatRequest catRequest = new CatRequest(ctx.message, user, chat);
        catRequest.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest);
    }

    public Context buildContext(Chat chat, Chat publicChat, Message message) {
        return new Context(chat, publicChat, message);
    }

    class Context extends PhaseContext {
        private Message message;
        private Chat publicChat;

        public Context(Chat chat, Chat publicChat, Message message) {
            super(chat);
            this.message = message;
            this.publicChat = publicChat;
        }
    }
}
