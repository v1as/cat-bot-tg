package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: Portenato
 * */
@Component
public class WerecatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Прекрасный летний вечер, солнце потихоньку клонится к закату",
                "Вы идете по проселочной дороге, вокруг шумят сосны. Вдруг перед вам возникает развилка,"
                        + " выбирать придется между узкой тропинкой и широкой дорогой ближе к реке");
        poll("Что выберете?")
                .choice("Широкая дорога", this::wideRoad)
                .choice("Узкая тропинка", this::smallRoad)
                .send();
    }

    private void wideRoad(ChooseContext chooseContext) {
        messages(
                "Широкая дорога заканчивается небольшим пляжем с сидящими на нем рыбаками."
                        + " Рядом с ведром рыбой вы замечаете кота. "
                        + "Он расчитывает незаметно стащить немного рыбы на ужин");
        poll("Может позвать кота?")
                .choice("Кот!", this::callCat)
                .choice("Наблюдать", this::justLook)
                .send();
    }

    private void callCat(ChooseContext chooseContext) {
        messages(
                "Вы пытаетесь привлечь внимание Кота, но привлекаете только внимание рыбака",
                "\"Брысь!\" - кричит он Коту",
                "Вы не успели его сосчитать");
        catchUpCatAndClose(NOT_CAT);
    }

    private void justLook(ChooseContext chooseContext) {
        messages(
                "Вы стоите и наблюдаете, как Кот тихонечко утаскивает рыбку, лежащую рядом с ведром");
        poll("Кажется, у кого-то будет вкусный ужин!")
                .choice("Кот!", c -> catchUpCatAndClose(CAT1))
                .choice("Наблюдать", this::justLook2)
                .send();
    }

    private void justLook2(ChooseContext chooseContext) {
        messages("Кот быстро и ловко утаскивает добычу в кусты.", "Кажется вы упустили кота");
        catchUpCatAndClose(NOT_CAT);
    }

    private void smallRoad(ChooseContext chooseContext) {
        messages(
                "Дорожка оказалась извилистой, она становится все уже и уже. Вы поняли, что забрели в темный сосновый бор.");
        poll("Тем временем солнце село и вокруг вас только только деревья, которые освещает полная луна. Вы включаете фонарик и видите тень в кустах. Что это?")
                .choice("Посмотреть", this::beCurious)
                .choice("Вернуться обратно", this::comeBack)
                .send();
    }

    private void beCurious(ChooseContext chooseContext) {
        messages(
                "Вы заглядываете в кусты. Хвост какой-то подозрительно большой для кота.. И луна как будто засияла еще ярче.",
                "Вам встретился Котооборотень!",
                "Но ведь это тоже кот, правда?");
        catchUpCatAndClose(CAT1);
    }

    private void comeBack(ChooseContext chooseContext) {
        messages("Быстрым шагом вы поспешно возвращаетесь в дачный поселок. Вот это приключение!");
        catchUpCatAndClose(NOT_CAT);
    }
}
