package ru.v1as.tg.cat.callbacks.phase;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class MirrorCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        timeout(1000);
        message("Как и всегда вы устремляетесь за котом");
        timeout(1000);
        message("На улице свежо и приятно. Кот трусит впереди.");
        timeout(1000);
        poll("Что будем делать?")
                .choice("Оглядеться", random(this::lookAround, this::lookAroundFail))
                .choice("Следуем дальше", this::followTheCat)
                .send();
    }

    private void lookAroundFail(ChooseContext chooseContext) {
        timeout(2000);
        message("Пока вы считали ворон по сторонами, кот сбежал.");
        timeout(2000);
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }

    private void lookAround(ChooseContext chooseContext) {
        timeout(2000);
        message("Вы осмотрелись по сторонам и вдохнули полной грудью.");
        timeout(1000);
        message(
                "Но долго расслабиться вам не удалось,"
                        + " недовольное мяуканье привлекло ваше внимание - кот потрусил дальше.");
        timeout(2000);
        poll("Что дальше?")
                .choice("Идём за котом", this::followTheCat)
                .choice("Считаем ворон дальше", this::amountRavens)
                .send();
    }

    private void followTheCat(ChooseContext chooseContext) {
        timeout(2000);
        message("Вы продолжаете следовать за котом.");
        timeout(2000);
        message("Котяра остановился и, оглянувшись на вас, начал лакать воду из лужи.");
        timeout(2000);
        poll("Что будем делать?")
                .choice("Подойти к коту", this::goToCat)
                .choice("Наблюдать", this::watchAtCat)
                .send();
    }

    private void watchAtCat(ChooseContext chooseContext) {
        timeout(2000);
        message("Наблюдая за котом, вы встречаетесь с ним взглядом в отражении.");
        timeout(2000);
        message(
                "Любопытный кот подмигивает вам и убегает. Как думаете,"
                        + " можно засчитать кота и его отражение за двоих?");
        timeout(1000);
        catchUpCatAndClose(chooseContext, CAT2);
    }

    private void goToCat(ChooseContext chooseContext) {
        timeout(1000);
        message("О чем вы только думали? Кот, раздраженно мяукая, сбегает");
        timeout(2000);
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }

    private void amountRavens(ChooseContext chooseContext) {
        timeout(1000);
        message("Вы продолжаете наслаждаться погодой.");
        timeout(1500);
        message("Кот, естесственно, не стал вас ждать и сбежал.");
        timeout(1500);
        message("Зато вы славно отдохнули.");
        timeout(1500);
        catchUpCatAndClose(chooseContext, NOT_CAT);
    }

}
