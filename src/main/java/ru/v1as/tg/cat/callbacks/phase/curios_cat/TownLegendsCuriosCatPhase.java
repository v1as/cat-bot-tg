package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

public class TownLegendsCuriosCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        message("Сегодня ваш c котом путь идёт через главную площадь города.");
        message("По праздникам и особым дням на ней запускают салюты и устраивают выступления.");
        message(
                "А сегодня на ней глаголит местный старик. "
                        + "Кто-то говорит, что он выжил из ума, а кто-то, что он был в своё время гением.");
        poll("Что будем делать?")
                .choice("Слушать легенды", this::listenLegends)
                .choice("Следовать за котом", random(this::follow, this::followFail))
                .send();
    }

    private void follow(ChooseContext chooseContext) {
        message("Голос рассказчика затихает за спиной.");
        message("Сегодня вам не суждено услышать удивительную историю.");
        message("Зато хоть кота засчитают.");
        catchUpCatAndClose(CatRequestVote.CAT1);
    }

    private void followFail(ChooseContext chooseContext) {
        message("Коту не слишком понравилось, что вы не захотели слушать старика.");
        message("Далее всё как всегда - недовольное мяуканье и исчезающий хвост.");
        catchUpCatAndClose(CatRequestVote.NOT_CAT);
    }

    private void listenLegends(ChooseContext chooseContext) {}
}
