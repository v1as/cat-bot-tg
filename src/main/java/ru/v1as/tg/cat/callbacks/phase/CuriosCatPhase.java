package ru.v1as.tg.cat.callbacks.phase;

import static java.time.temporal.ChronoUnit.SECONDS;

import java.time.Duration;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

public class CuriosCatPhase extends AbstractPhase {

    private static final PollTimeoutConfiguration TIMEOUT_LEAVE_CAT =
            new PollTimeoutConfiguration(Duration.of(10, SECONDS))
                    .removeMsg(true)
                    .message("Любопытный кот убежал");

    private Message curiosCatMessage;
    private ScoreData scoreData;

    public CuriosCatPhase(
            UnsafeAbsSender sender,
            TgCallbackProcessor callbackProcessor,
            ChatData chat,
            ScoreData scoreData) {
        super(sender, callbackProcessor, chat);
        this.scoreData = scoreData;
    }

    @Override
    public void open() {
//        poll("Любопытный кот гуляет рядом")
//                .removeOnChoice(true)
//                .choice(EmojiConst.CAT + " Кот!", this::sayCat)
//                .choice("Пойти за котом", this::goToCat)
//                .addOnSend(msg -> curiosCatMessage = msg)
//                .timeout(new PollTimeoutConfiguration(Duration.of(3, MINUTES)).removeMsg(true))
//                .send();
    }

//    private void sayCat(PollChoiceData data){
//        saveCatRequest(user);
//        sendMessage("Любопытный кот убежал к " + user.getUsernameOrFullName());
//        close();
//    }
//
//    private void goToCat(PollChoiceData data){
//        privatePoll(user, "Кот неторопливо бежит впереди вас")
//                .choice("Попытаться догнать кота", this::fastFollowCat)
//                .choice("Спокойно следовать", this::followTheCat)
//                .timeout(TIMEOUT_LEAVE_CAT);
//    }
//
//    private void fastFollowCat(PollChoiceData data){
//        sendMessage(
//                user,
//                "Кот испугался и рванул что было сил, вам не удалось его догнать."
//                        + " Хуже того, никто не слышал как вы кричали 'Кот'.");
//        close();
//    }
//
//    private void followTheCat(PollChoiceData data){
//        privatePoll(
//                        user,
//                        "Вы продолжаете осторожно следовать за котом, пристально следя за ним взглядом. "
//                                + "Боковым зрением вы вдруг замечаете, как что-то блестит на дороге")
//                .choice("Разглядеть находку", this::resStone)
//                .choice("Не отвлекаться", this::catchCat)
//                .timeout(TIMEOUT_LEAVE_CAT);
//    }
//
//    private void resStone(PollChoiceData data){
//        sendMessage(
//                user,
//                "Вы остановились чтобы разглядеть находку. "
//                        + "Это оказался затейливый красный камешек,"
//                        + " пожалуй вы заберёте его себе - в хозяйстве всё пригодится.");
//        timeoutSeconds(3);
//        sendMessage(
//                String.format("Игрок '%s' находит красный камень", user.getUsernameOrFullName()));
//        sendMessage(
//                user,
//                "Пока вы разглядывали драгоценность кота и след простыл,"
//                        + " хотя в воздухе остался лишь след улыбки кота."
//                        + " Похоже он не просто так вас сюда привёл.");
//        close();
//    }
//
//    private void catchCat(PollChoiceData data){
//        sendMessage(
//                user, "Кот остановился, и внимательно посмотрел на вас, похоже вы его не поняли.");
//        timeoutSeconds(3);
//        sendMessage(user, "Любопытный кот подбежал и потёрся о вашу ногу.");
//        sendMessage("Любопытный кот убегает к " + user.getUsernameOrFullName());
//        saveCatRequest(user);
//        close();
//    }
//
//    private void saveCatRequest(PollChoiceData data){
//        CatRequest catRequest = new CatRequest(curiosCatMessage, user, chat);
//        catRequest.finish(CatRequestVote.CAT1);
//        scoreData.save(catRequest);
//    }
}
