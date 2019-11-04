package ru.v1as.tg.cat.callbacks.phase;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class MadCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        timeout(1000);
        message("В этот раз точно что-то не так. Кот бежит явно быстрее обычного.");
        timeout(1000);
        poll("Что делаем?")
                .choice("Ускоряем шаг", this::goFaster)
                .choice("Идём спокойно", this::justGo)
                .send();
    }

    private void justGo(ChooseContext chooseContext) {
        timeout(1000);
        message(
                "Вы продолжаете идти как и шли, а странный кот скрывается за поворотом. Туда ему и дорога.");
        timeout(1000);
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }

    private void goFaster(ChooseContext chooseContext) {
        timeout(1000);
        message("Кот останаливается и начинает на вас шипеть!");
        timeout(2000);
        poll("Что дальше?")
                .choice("Ретироваться", this::runAway)
                .choice("Стоять на месте", this::stand)
                .send();
    }

    private void stand(ChooseContext chooseContext) {
        timeout(1000);
        message(" О нет, похоже, вы обознались, и это не любопытный кот, это бешеный кот!");
        timeout(3000);
        message(
                "Вы отправляетесь восвояси с расцарапанным лицом. Вероятно, вам следует обратиться к врачу.");
        timeout(3000);
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }

    private void runAway(ChooseContext chooseContext) {
        timeout(1000);
        message("Вы осторожно отходите от кота.");
        timeout(2000);
        message("Странный кот, не прекращая шипеть, галопом умчался.");
        timeout(2000);
        message("Что ж, могло бы быть и хуже.");
        timeout(2000);
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }
}
