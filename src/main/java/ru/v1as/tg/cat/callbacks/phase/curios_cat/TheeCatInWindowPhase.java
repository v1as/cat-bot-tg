package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class TheeCatInWindowPhase extends AbstractCuriosCatPhase {

    private static final String CATS = "CATS";

    @Override
    protected void open() {
        messages(
                "В этот раз Любопытный Кот выглядит довольно целеустремлённым.",
                "Вы почти что слышите уверенный топот кошачьих лапок.",
                "Проходя мимо торца здания, вы замечаете кота в окне.");
        poll("Как поступим?")
                .choice("Кот! " + EmojiConst.CAT, this::cat1)
                .choice("Следуем дальше", this::follow1)
                .send();
    }

    private void follow1(ChooseContext chooseContext) {
        messages(
                "Кот всё так же серьёзно настроен и бежит вперёд.",
                "Вот уже второй кот, подобравшись, смотрит на вас из окна.");
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
        messages(
                "Пушистый путеводитель даже ни разу не обернулся на вас за эту прогулку.",
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
        message("Последнее, что вы увидели - взмах пушистого хвоста.");
        Integer cats = getPhaseContext().get(CATS, 0);
        if (cats == 0) {
            message(
                    "Зря вы так ни разу не указали на котов, которых вам показывали. "
                            + "Ну ничего, в следующий раз. Может быть.");
            catchUpCatAndClose(CatRequestVote.NOT_CAT);
        } else if (cats == 1) {
            message("Что ж, плюс один кот - совсем неплохо");
            catchUpCatAndClose(CatRequestVote.CAT1);
        } else if (cats == 2) {
            message("Плюс два кота - это прям хорошо!");
            catchUpCatAndClose(CatRequestVote.CAT2);
        } else if (cats == 3) {
            message("Вы - молодец. Любопытный кот гордится вами!");
            catchUpCatAndClose(CatRequestVote.CAT3);
        } else {
            catchUpCatAndClose(CatRequestVote.NOT_CAT);
        }
    }

    private void cat3(ChooseContext chooseContext) {
        getPhaseContext().increment(CATS);
        this.follow3(chooseContext);
    }
}
