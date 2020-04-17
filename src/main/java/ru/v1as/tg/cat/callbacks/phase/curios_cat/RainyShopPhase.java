package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

/*
 * author: Portenato
 * */
@Component
public class RainyShopPhase extends AbstractCuriosCatPhase {

    protected static final RandomRequest<CatRequestVote> RANDOM_REQUEST_CAT_0_2_3 =
            new RandomRequest<CatRequestVote>().add(NOT_CAT, 40).add(CAT2, 40).add(CAT3, 20);

    @Override
    protected void open() {
        messages(
                "Мерзкая погода и темнота накрыли город.",
                "У вас осталось всего 10 минут, чтобы успеть в магазин, и вдруг в темном дворе видите Любопытного Кота",
                "Он куда-то спешит");
        poll("Как поступим?")
                .choice("Попытаться успеть в магазин", this::goShopping)
                .choice("Побежать за Котом", this::runAfter)
                .send();
    }

    private void goShopping(ChooseContext chooseContext) {
        messages(
                "В последние 5 минут вы успеваете забежать в магазин.",
                "Может быть взять немного кошачьей еды?",
                ("С батоном хлеба и кошачьим кормом вы выходите на улицу и медленно идете через двор,"
                        + " в надежде встретить Кота еще раз."));
        CatRequestVote cat = random(RANDOM_REQUEST_CAT_0_2_3);
        if (NOT_CAT.equals(cat)) {
            messages("В такую плохую погоду во дворе нет ни одного кота, вы зря потратили деньги");
        } else if (CAT2.equals(cat)) {
            messages(
                    "Во дворе вы видите миски для корма, но рядом с ними никого нет",
                    "Что поделать, придется просто оставить корм в миске, но, стойте,"
                            + " к вам бежит маленький голодный котенок");
        } else if (CAT3.equals(cat)) {
            messages(
                    "Во дворе вы видите миски для корма,"
                            + "Любопытный Кот и 2 его друга уже ждут пока вы их покормите");
        }
        catchUpCatAndClose(cat);
    }

    private void runAfter(ChooseContext chooseContext) {
        messages(
                "Вы бежите через темный двор, наступая в лужи, Кот шмыгает в подвальное окно.",
                "Кажется, в этот раз вам не удалось его поймать. Может в следующий раз удача будет благосклоннее?");
    }
}
