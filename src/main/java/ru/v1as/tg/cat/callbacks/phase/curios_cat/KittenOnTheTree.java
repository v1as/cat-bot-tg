package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
public class KittenOnTheTree extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Любопытный Кот в этот раз определённо взволнован.",
                "Вы почти что видите тревогу на его мордочке.",
                "Темп прогулки задан нешуточный - вы разве что не переходите на бег.");
        poll("Не устали идти так быстро?")
                .choice("Сбавить темп", this::goSlower)
                .choice("Сохранить темп", this::keepSpeed)
                .send();
    }

    private void goSlower(ChooseContext ctx) {
        messages(
                "Вы сбавили темп и что-то воскликнули в след коту, неопределённо взмахнув рукой.",
                "Кот остановился и ждёт вас.",
                "Вглядываясь в его выражение, вы определённо различаете тревогу. ",
                "Дело то на этот раз важное, может всё же стоило поспешить?");
        keepSpeed(ctx);
    }

    private void keepSpeed(ChooseContext ctx) {
        messages(
                "Некая напряженность передалась и вам.",
                "Прогулка завершилась для вас довольно так же неожиданно, как и началась.",
                "Вы с котом стоите возле жилого дома.");
        poll("...").choice("Оглядеться", this::lookAround).send();
    }

    private void lookAround(ChooseContext chooseContext) {
        messages(
                "Долго разбираться не пришлось.",
                "Над головой раздаётся тихое мяуканье, а взгляд кошачей взволнованной морды направлен вверх.",
                "В кроне дерева сидит котёнок и растерянно мяучит.");
        poll("Как поступим?")
                .choice("Котёнок! " + EmojiConst.CAT, this::kittenCount)
                .choice("Спасаем котёнка", this::helpKitten)
                .send();
    }

    private void helpKitten(ChooseContext chooseContext) {
        final Consumer<ChooseContext> nextChoice =
                random(
                        new RandomRequest<Consumer<ChooseContext>>()
                                .add(this::successHelp, 70)
                                .add(this::failHelp, 30));
        messages(
                "Что же, придётся лезть на дерево.",
                "Довольно неуклюже и более чем самонадеянно вы карабкаетесь на дерево.",
                "Крепкие и надёжные ветки закончились и вам придётся выбрать за какую ухватиться сейчас.",
                "Левая - потолще, но сухая.",
                "Правая - потоньше, но выглядит поживее.");
        poll("Какую ветку выберем?")
                .choice("Левую", nextChoice)
                .choice("Правую", nextChoice)
                .send();
    }

    private void successHelp(ChooseContext chooseContext) {
        messages(
                "Ветку вы выбрали довольно крепкую.",
                "Не встретив более препятствий, вы добираетесь до котёнка и сажаете его себе за пазуху.",
                "Маленький пушистый комочек трясётся у вас под курткой.",
                "Спустивший вниз вы опускаете котёнка на землю.");
        finish();
    }

    private void finish() {
        message(
                "Любопытный Кот, благодарно кивнув вам, хватает зубами котёнка за шкирку и убегает.");
        catchUpCatAndClose(CAT3);
    }

    private void failHelp(ChooseContext chooseContext) {
        messages(
                "В этот раз выбор был не слишком успешным.",
                "Ветка ломается у вас в руках и вы падаете на землю.",
                "Повезло, что вы забрались не так высоко - на этот раз обойдётся без сломанных костей.",
                "Сверху на вас падает пушистый комочек. Котёнок, испугавшись треска сломанной ветки, сорвался, но ему повезло с приземлением.");
        finish();
    }

    private void kittenCount(ChooseContext chooseContext) {
        messages("Хороший выбор.", "Два балла - есть два балла.");
        catchUpCatAndClose(CAT2);
    }
}
