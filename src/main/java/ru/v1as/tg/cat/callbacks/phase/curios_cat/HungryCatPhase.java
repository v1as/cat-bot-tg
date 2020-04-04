package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class HungryCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "В этот раз кот никуда не идёт.",
                "Да и выглядит он каким-то...",
                "Голодным.",
                "Случайность ли, но совсем рядом продуктовый магазин.");
        poll("Покормим котика?")
                .choice("Покормить", this::buyMeal)
                .choice("Кот! " + EmojiConst.CAT, this::catchTheCat)
                .send();
    }

    private void catchTheCat(ChooseContext chooseContext) {
        messages(
                "Кот смотрит на вас взглядом, полным вселенской скорби.",
                "Но вы не поддаётесь, и получаете свой заслуженный балл.");
        catchUpCatAndClose(CAT1);
    }

    private void buyMeal(ChooseContext chooseContext) {
        messages(
                "Выходя из магазина, вы довольно размахиваете пакетиком с кормом.",
                "Вы горды собой и предвкушаете как сделаете доброе дело.");
        Consumer<ChooseContext> next = random(this::feedTheCat, this::feedTheCatFail);
        next.accept(chooseContext);
    }

    private void feedTheCat(ChooseContext chooseContext) {
        messages(
                "Кот сразу же замечает угощенье в ваших руках.",
                "Он вьётся вокруг ваших ног, а его мяуканье слышно на соседней улице.",
                "Наблюдая за тем, как содержимое пакетика тает на глазах, вы получаете свой заслуженный балл.");
        catchUpCatAndClose(CAT1);
    }

    private void feedTheCatFail(ChooseContext chooseContext) {
        messages(
                "Но, оказавшись на улице, вы понимаете, что остались одни.",
                "Видимо, кот был не так уж и голоден.");
        catchUpCatAndClose(NOT_CAT);
    }
}
