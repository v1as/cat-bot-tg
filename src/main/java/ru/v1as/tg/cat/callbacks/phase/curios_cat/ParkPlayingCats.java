package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.BOX;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.FOUNTAIN;
import static ru.v1as.tg.cat.EmojiConst.PARK;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.fromAmount;

import java.time.Duration;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class ParkPlayingCats extends AbstractCuriosCatPhase {

    public static final String PLAYING_CATS_STEP = "PLAYING_CATS_STEP";
    public static final String PLAYING_CATS = "PLAYING_CATS";

    @Override
    protected void open() {
        messages(
                "Сегодня вам очень не хочется сидеть дома.",
                "Вы решили немного размять ноги и пошли прогуляться.");
        poll("Куда пойдём?")
                .choice("В парк " + PARK, this::park)
                .choice("На главную площадь " + FOUNTAIN, this::square)
                .send();
    }

    private void park(ChooseContext chooseContext) {
        messages(
                "Вы сворачиваете под кроны деревьев.",
                "Весело поют птички.",
                "Тут впереди вы замечаете Игривых Котов.",
                "Они бегают по полянке друг за другом, постоянно прячась за деревьями или в высокой траве.",
                "Успеете ли вы их всех посчитать?");
        scheduleRandomCats(chooseContext);
    }

    private void square(ChooseContext chooseContext) {
        messages(
                "Вы идёте к центру вашего небольшого городка.",
                "На главной площади вы замечаете какое-то оживление.",
                "Подходите поближе, а там лотерея котов!",
                "Вам предлагают сыграть в игру: перед вами пять коробок, нужно угадать,"
                        + " в какой из них сидит Кот, а может даже и не один!",
                "Коробки переставлены местами, начинаем!");
        poll("Итак, какую коробку выбираем?")
                .choice("1 " + BOX, this::box)
                .choice("2 " + BOX, this::box)
                .choice("3 " + BOX, this::box)
                .choice("4 " + BOX, this::box)
                .choice("5 " + BOX, this::box)
                .send();
    }

    private void scheduleRandomCats(ChooseContext chooseContext) {
        botClock.schedule(
                contextWrap(() -> randomCats(chooseContext)),
                (long) (Math.random() * 4000 + 1000),
                TimeUnit.MILLISECONDS);
    }

    private void randomCats(ChooseContext chooseContext) {
        final Integer catsStep = getPhaseContext().increment(PLAYING_CATS_STEP);
        if (catsStep >= 3) {
            finishRandomCats(chooseContext);
        } else {
            final PollTimeoutConfiguration timeout =
                    new PollTimeoutConfiguration(
                                    Duration.ofMillis((long) (Math.random() * 1000 + 500)))
                            .removeMsg(true)
                            .onTimeout(() -> scheduleRandomCats(chooseContext));
            poll("...")
                    .choice(
                            "Кот! " + CAT,
                            c -> {
                                getPhaseContext().increment(PLAYING_CATS);
                                scheduleRandomCats(c);
                            })
                    .timeout(timeout)
                    .send();
        }
    }

    private void finishRandomCats(ChooseContext chooseContext) {
        final CatRequestVote cats = fromAmount(getPhaseContext().get(PLAYING_CATS, 0));
        switch (cats) {
            case NOT_CAT:
                message("Что ж, сегодня вам не повезло, но всегда можно прогуляться ещё раз!");
                break;
            case CAT1:
                message("Неплохо, один же лучше, чем ноль?");
                break;
            case CAT2:
                message("Да вы мастер замечать Котов!");
                break;
            case CAT3:
                message("Вот это да, быть не может, но вы нашли всех Котов!");
                break;
        }
        catchUpCatAndClose(cats);
    }

    private void box(ChooseContext chooseContext) {
        final CatRequestVote cats = random(RANDOM_REQUEST_CAT_0_1_2_3);
        switch (cats) {
            case NOT_CAT:
                message("Что ж, это был ваш выбор, очень жаль, но вы не выиграли");
                break;
            case CAT1:
                messages("Не очень хорошо, но и не плохо, целый один Кот!");
                break;
            case CAT2:
                messages(
                        "Что за плюшевый комочек смотрит на вас?"
                                + " Милый трёцветный котёнок, которого вы забираете себе");
                break;
            case CAT3:
                messages("Да вы везунчик, каких поискать!");
                break;
        }
        catchUpCatAndClose(cats);
    }
}
