package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class MadCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        message("В этот раз точно что-то не так. Кот бежит явно быстрее обычного.");
        poll("Что делаем?")
                .choice("Ускоряем шаг", this::goFaster)
                .choice("Идём спокойно", this::justGo)
                .send();
    }

    private void justGo(ChooseContext chooseContext) {
        message(
                "Вы продолжаете идти как и шли, а странный кот скрывается за поворотом. Туда ему и дорога.");
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }

    private void goFaster(ChooseContext chooseContext) {
        message("Кот останаливается и начинает на вас шипеть!");
        poll("Что дальше?")
                .choice("Ретироваться", this::runAway)
                .choice("Стоять на месте", this::stand)
                .send();
    }

    private void stand(ChooseContext chooseContext) {
        message(" О нет, похоже, вы обознались, и это не любопытный кот, это бешеный кот!");
        message(
                "Вы отправляетесь восвояси с расцарапанным лицом. Вероятно, вам следует обратиться к врачу.");
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }

    private void runAway(ChooseContext chooseContext) {
        message("Вы осторожно отходите от кота.");
        message("Странный кот, не прекращая шипеть, галопом умчался.");
        message("Что ж, могло бы быть и хуже.");
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }
}
