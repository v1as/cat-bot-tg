package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

/*
 * author: AnnaTemnaya
 * */
@Component
public class FromHomeWindowPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Вы сидите в комнате и смотрите в окно",
                "Взгляд блуждает по детской площадке и останавливается у мусорных баков",
                "Там голодный Любопытный Кот высматривает себе ужин");
        poll("Что делаем?")
                .choice("Кот!", this::cat)
                .choice("Продолжаем наблюдать", this::keepWatch)
                .send();
    }

    private void cat(ChooseContext chooseContext) {
        catchUpCatAndClose(CAT1);
    }

    private void keepWatch(ChooseContext chooseContext) {
        messages(
                "Любопытный Кот отошёл так, что его стало не видно из окна",
                "Вы прижались лбом к стеклу, но всё равно не можете снова его увидеть");
        poll("Что делаем?")
                .choice("Ждём, пока Кот вернётся в поле зрения", this::waitCat)
                .choice("Идём на балкон", this::balcony)
                .send();
    }

    private void waitCat(ChooseContext chooseContext) {
        messages(
                "Но Кот так и не вернулся",
                "Устав сидеть в неудобной позе, вы идете дальше смотреть сериальчик");
        catchUpCatAndClose(NOT_CAT);
    }

    private void balcony(ChooseContext chooseContext) {
        messages("Выбежав на балкон, высовываетесь из окна");
        random(
                        new RandomRequest<Consumer<ChooseContext>>()
                                .add(this::oneCat, 66)
                                .add(this::threeCat, 33))
                .accept(chooseContext);
    }

    private void threeCat(ChooseContext chooseContext) {
        messages(
                "Это был не Кот, а Кошка!",
                "Теперь она с котёнком ест оставленную кем-то еду в лоточке",
                "Вдоволь полюбовавшись на них, идёте в квартиру");
        catchUpCatAndClose(CAT3);
    }

    private void oneCat(ChooseContext chooseContext) {
        messages(
                "Да, так вы снова видите Кота",
                "Он ест оставленную кем-то еду",
                "Что ж, теперь он сыт, да и вам пора перекусить");
        catchUpCatAndClose(CAT1);
    }

}
