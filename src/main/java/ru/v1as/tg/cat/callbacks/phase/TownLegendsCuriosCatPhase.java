package ru.v1as.tg.cat.callbacks.phase;

import static ru.v1as.tg.cat.utils.RandomUtils.random;

import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

public class TownLegendsCuriosCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        timeout(3000);

        message("Сегодня ваш c котом путь идёт через главную площадь города.");
        timeout(3000);

        message("По праздникам и особым дням на ней запускают салюты и устраивают выступления.");
        timeout(3000);

        message(
                "А сегодня на ней глаголит местный старик. "
                        + "Кто-то говорит, что он выжил из ума, а кто-то, что он был в своё время гением.");
        timeout(3000);

        poll("Что будем делать?")
                .choice("Слушать легенды", this::listenLegends)
                .choice("Следовать за котом", random(this::follow, this::followFail))
                .send();
    }

    private void follow(ChooseContext chooseContext) {
        timeout(3000);

        message("Голос рассказчика затихает за спиной.");
        timeout(2000);

        message("Сегодня вам не суждено услышать удивительную историю.");
        timeout(2000);

        message("Зато хоть кота засчитают.");
        timeout(2000);

        catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
    }

    private void followFail(ChooseContext chooseContext) {
        timeout(3000);

        message("Коту не слишком понравилось, что вы не захотели слушать старика.");
        timeout(3000);

        message("Далее всё как всегда - недовольное мяуканье и исчезающий хвост.");
        timeout(2000);
        catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
    }

    private void listenLegends(ChooseContext chooseContext) {

    }
}
