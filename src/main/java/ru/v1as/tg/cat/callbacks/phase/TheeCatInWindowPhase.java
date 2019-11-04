package ru.v1as.tg.cat.callbacks.phase;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class TheeCatInWindowPhase extends AbstractCuriosCatPhase {

    private static final String CATS = "CATS";

    @Override
    protected void open() {
        timeout(2000);
        message("В этот раз Любопытный Кот выглядит довольно целеустрёмлённым.");
        timeout(3000);
        message("Вы почти что слышите уверенный топот кошачьих лапок.");
        timeout(3000);
        message("Проходя мимо торца здания, вы замечаете кота в окне");
        timeout(3000);
        poll("Как поступим?")
                .choice("Кот! " + EmojiConst.CAT, this::cat1)
                .choice("Следуем дальше", this::follow1)
                .send();
    }

    private void follow1(ChooseContext chooseContext) {
        timeout(2000);
        message("Кот всё так же серьёзно настроен и бежит вперёд.");
        timeout(3000);
        message("Вот уже второй кот, подоравшись, смотрит на вас из окна.");
        poll("Что делаем?")
                .choice("Кот! " + EmojiConst.CAT, this::cat2)
                .choice("Следуем дальше", this::follow2)
                .send();
    }

    private void cat1(ChooseContext chooseContext) {
        getPhaseContext().increment(CATS);
        this.follow1(chooseContext);
    }

    private void follow2(ChooseContext chooseContext) {
        timeout(3000);
        message("Пушистый путеводитель даже ни разу не обернулся на вас за эту прогулку.");
        timeout(4000);
        message(
                "Это утро, видимо, какое-то особенное - уже третий кот устроился на подоконнике, поджав лапки.");
        poll("Что дальше?")
                .choice("Кот! " + EmojiConst.CAT, this::cat3)
                .choice("Следуем дальше", this::follow3)
                .send();
    }

    private void cat2(ChooseContext chooseContext) {
        getPhaseContext().increment(CATS);
        this.follow2(chooseContext);
    }

    private void follow3(ChooseContext chooseContext) {
        timeout(2000);
        message("Последнее, что вы увидели - взмах пушистого хвоста.");
        timeout(3000);
        Integer cats = getPhaseContext().get(CATS, 0);
        if (cats == 0) {
            message(
                    "Зря вы так ни разу не указали на котов, которых вам показывали. "
                            + "Ну ничего, в следующий раз. Может быть.");
            catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
        } else if (cats == 1) {
            message("Что ж, плюс один кот - совсем неплохо");
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT1);
        } else if (cats == 2) {
            message("Плюс два кота - это прям хорошо!");
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT2);
        } else if (cats == 3) {
            message("Вы - молодец. Любопытный кот гордится вами!");
            catchUpCatAndClose(chooseContext, CatRequestVote.CAT3);
        } else {
            catchUpCatAndClose(chooseContext, CatRequestVote.NOT_CAT);
        }
    }

    private void cat3(ChooseContext chooseContext) {
        getPhaseContext().increment(CATS);
        this.follow3(chooseContext);
    }
}
