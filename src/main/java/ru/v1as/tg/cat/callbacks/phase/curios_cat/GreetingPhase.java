package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.EmojiConst.HI_HAND;
import static ru.v1as.tg.cat.EmojiConst.NO_EYES_MONKEY;
import static ru.v1as.tg.cat.EmojiConst.OFFICE;
import static ru.v1as.tg.cat.EmojiConst.PHOTO_WITH_LIGHT;
import static ru.v1as.tg.cat.EmojiConst.STADIUM;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: @ElkaLoveRub
 */
@Component
public class GreetingPhase extends AbstractCuriosCatPhase {

    public static final String HI = "HI";

    @Override
    protected void open() {
        messages(
                "Вы решили выйти прогуляться, может сегодня вам улыбнется кошачий дух.",
                "Не успели вы повернуть за угол своего дома, как заметили друга.",
                "Поздороваемся или сделаем вид, что не заметили?");
        poll("Как поступим?")
                .choice("Поздароваться " + HI_HAND, this::hi)
                .choice("Спрятаться " + NO_EYES_MONKEY, this::ignore)
                .send();
    }

    private void hi(ChooseContext chooseContext) {
        getPhaseContext().set(HI, true);
        message("Решение не заставило себя ждать, идём гулять вместе.");
        poll("Куда пойдем?")
                .choice("Во дворы " + OFFICE, this::yard)
                .choice("На стадион " + STADIUM, this::stadium)
                .send();
    }

    private void ignore(ChooseContext chooseContext) {
        getPhaseContext().set(HI, false);
        message("Затаив дыхание и опустив голову, вы медленно пошли прочь.");
        poll("Только куда?")
                .choice("Во дворы " + OFFICE, this::yard)
                .choice("На стадион " + STADIUM, this::stadium)
                .send();
    }

    private void yard(ChooseContext chooseContext) {
        poll("Спустя десять минут прогулки вы замечаете кота в окне.")
                .choice("Подойти поближе и сфотографировать " + PHOTO_WITH_LIGHT, this::photo)
                .choice("Кот! " + CAT, this::cat)
                .send();
    }

    private void photo(ChooseContext chooseContext) {
        final Boolean hi = getPhaseContext().get(HI);
        if (hi) {
            messages(
                    "Вы начинаете приближаться к цели, но тут из-за спины доносится \"Кот!\"",
                    "Как вы могли забыть - ваш друг тоже в теме!",
                    "Ну ничего, может следующий кот ваш.");
            catchUpCatAndClose(NOT_CAT);
        } else {
            messages(
                    "Вы начинаете приближаться к цели,но тут рядом с котом в окне появляется его хозяйка.",
                    "Вы хватаете телефон, быстро сфотографировав, кота убегаете дальше.");
            catchUpCatAndClose(CAT1);
        }
    }

    private void cat(ChooseContext chooseContext) {
        messages(
                "Выкрикнув кот, вы испугали окружающих и котёнка, который лежал рядом с мамой на окне.",
                "Ваш крик заставил его поднять голову. Похоже вы счастливчик.");
        catchUpCatAndClose(CAT3);
    }

    private void stadium(ChooseContext chooseContext) {
        messages(
                "Сегодня замечательная погода. На стадионе полно людей.",
                " Надо было взять с собой мячик,а не кошачий корм.");
        catchUpCatAndClose(NOT_CAT);
    }
}
