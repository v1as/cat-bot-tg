package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.callbacks.phase.curios_cat.IsolationOrWindowPhase.ISOLATION_AWARE;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.random.RandomRequest;

/*
 * author: AnnaTemnaya
 * */
@Component
public class IsolationShopOrParkPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                ISOLATION_AWARE,
                "Просидев дома 9 дней, вы решили по-быстрому сбегать в магазин за хлебом и чипсиками",
                "Щурясь от солнца, осторожно оглядываетесь",
                "Кошачий хвост скрылся за углом дома, но в магазин вам идти по прямой");
        poll("Что делаем?")
                .choice("Идем за угол дома", this::houseCorner)
                .choice("Идем прямо в магазин", this::toTheShop)
                .send();
    }

    private void houseCorner(ChooseContext chooseContext) {
        messages("Вы спешите за хвостом", "Любопытный Кот уже далеко впереди вас, бежит к парку");
        poll("Что делаем?")
                .choice("Переходим на бег", this::startRunning)
                .choice(
                        "Не меняем темп",
                        random(
                                new RandomRequest<Consumer<ChooseContext>>()
                                        .add(this::keepSpeed)
                                        .add(this::keepSpeed2)))
                .send();
    }

    private void startRunning(ChooseContext chooseContext) {
        messages(
                "Вас останавливает полиция - в парке нет ни магазинов, ни аптек, а просто гулять немножечко нельзя",
                "Получив штраф, грустно идете обратно");
        catchUpCatAndClose(NOT_CAT);
    }

    private void keepSpeed(ChooseContext chooseContext) {
        messages(
                "Не меняя темпа, заходите в парк, оглядываетесь в поисках Кота",
                "Любопытный кот сидит под кустом и смотрит на вас",
                "Вы достаёте телефон, чтобы сделать фото",
                "Но в чатике сейчас закрыты фото котов!");
        final RandomRequest<Consumer<ChooseContext>> randomRequest =
                new RandomRequest<Consumer<ChooseContext>>()
                        .add(this::keepSpeedFail)
                        .add(this::keepSpeedSuccess);
        random(randomRequest).accept(chooseContext);
    }

    private void keepSpeedSuccess(ChooseContext chooseContext) {
        message("Вы такая няшка, что для вас сделали исключение!");
        catchUpCatAndClose(CAT1);
    }

    private void keepSpeedFail(ChooseContext chooseContext) {
        message("Никаки исключений на карантине!");
        catchUpCatAndClose(NOT_CAT);
    }

    private void keepSpeed2(ChooseContext chooseContext) {
        messages(
                "Ни Любопытного Кота, ни намёка на него нет и в помине",
                "Наверное, стоило немного пробежаться");
        catchUpCatAndClose(NOT_CAT);
    }

    private void toTheShop(ChooseContext chooseContext) {
        messages(
                "Без приключений доходите до магазина",
                "Купив хлеб, чипсы и баночку колы, возвращаетесь домой",
                "Зато прогулялись!");
        catchUpCatAndClose(NOT_CAT);
    }
}
