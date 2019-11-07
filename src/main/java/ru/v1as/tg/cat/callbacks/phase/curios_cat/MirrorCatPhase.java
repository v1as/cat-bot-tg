package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class MirrorCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        message("Как и всегда вы устремляетесь за котом");
        message("На улице свежо и приятно. Кот трусит впереди.");
        poll("Что будем делать?")
                .choice("Оглядеться", random(this::lookAround, this::lookAroundFail))
                .choice("Следуем дальше", this::followTheCat)
                .send();
    }

    private void lookAroundFail(ChooseContext chooseContext) {
        message("Пока вы считали ворон по сторонами, кот сбежал.");
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }

    private void lookAround(ChooseContext chooseContext) {
        message("Вы осмотрелись по сторонам и вдохнули полной грудью.");
        message(
                "Но долго расслабиться вам не удалось,"
                        + " недовольное мяуканье привлекло ваше внимание - кот потрусил дальше.");
        poll("Что дальше?")
                .choice("Идём за котом", this::followTheCat)
                .choice("Считаем ворон дальше", this::amountRavens)
                .send();
    }

    private void followTheCat(ChooseContext chooseContext) {
        message("Вы продолжаете следовать за котом.");
        message("Котяра остановился и, оглянувшись на вас, начал лакать воду из лужи.");
        poll("Что будем делать?")
                .choice("Подойти к коту", this::goToCat)
                .choice("Наблюдать", this::watchAtCat)
                .send();
    }

    private void watchAtCat(ChooseContext chooseContext) {
        message("Наблюдая за котом, вы встречаетесь с ним взглядом в отражении.");
        message(
                "Любопытный кот подмигивает вам и убегает. Как думаете,"
                        + " можно засчитать кота и его отражение за двоих?");
        catchUpCatAndClose(chooseContext, CAT2);
    }

    private void goToCat(ChooseContext chooseContext) {
        message("О чем вы только думали? Кот, раздраженно мяукая, сбегает");
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }

    private void amountRavens(ChooseContext chooseContext) {
        message("Вы продолжаете наслаждаться погодой.");
        message("Кот, естесственно, не стал вас ждать и сбежал.");
        message("Зато вы славно отдохнули.");
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }
}
