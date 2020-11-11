package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: Portenato
 * */
@Component
public class CatapultPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Утро в дачных поселках обычно идет спокойно и размеренно. Но не сегодня.",
                "Сегодня часть соседей проснулась от жалобного кошачьего ора. Кот залез на дерево и не может спуститься");
        poll("Как помочь бедолаге?")
                .choice("Полезть на дерево", this::goClimb)
                .choice("Нагнуть дерево веревкой", this::pullBack)
                .send();
    }

    private void goClimb(ChooseContext chooseContext) {
        messages(
                "Ветка за веткой и вот вы уже почти на верхушке дерева.",
                "Кот благодарен вам за спасение");
        catchUpCatAndClose(CAT1);
    }

    private void pullBack(ChooseContext chooseContext) {
        messages(
                "Решено, нужно просто привязать веревку к дереву и нагнуть березку с помощью автомобиля",
                "А там кот спрыгнет или вы его снимете");
        randomWay(chooseContext, this::pullBackFail, this::pullBackSuccess);
    }

    private void pullBackSuccess(ChooseContext chooseContext) {
        messages(
                "Вы четко следуете плану без каких-либо происшествий.",
                "Кот спрыгивает с ветки и с паническим мяуканьем убегает в кусты.");
        catchUpCatAndClose(CAT1);
    }

    private void pullBackFail(ChooseContext chooseContext) {
        messages(
                "Проблема только в том, что веревка оказалась жиденькая и лопнула в момент, когда береза почти коснулась земли",
                "Отличная катапульта вышла. Кот скрылся за соседними деревьями.");
        catchUpCatAndClose(NOT_CAT);
    }
}
