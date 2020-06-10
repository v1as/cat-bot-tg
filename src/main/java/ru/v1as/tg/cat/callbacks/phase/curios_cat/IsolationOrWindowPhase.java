package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: Portenato
 * */
@Component
public class IsolationOrWindowPhase extends AbstractCuriosCatPhase {
    public static final String ISOLATION_AWARE = "[☣️ Будьте осторожны, в данном квесте действует режим самоизоляции]";

    @Override
    protected void open() {
        messages(
                ISOLATION_AWARE,
                "На улице сегодня отличная погода: светит солнце и на деревьях пробивается молодая листва",
                "Вы смотрите в окно и наслаждаетесь чудесным днем");
        poll("Может пора прогуляться?")
                .choice("Выйти прогуляться", this::goOut)
                .choice("Продолжить смотреть в окно", this::lookAround)
                .send();
    }

    private void goOut(ChooseContext chooseContext) {
        messages(
                "Вы выходите на улицу, заворачиваете за угол и, кажется, видите вдалеке мелькнувший хвостик Кота",
                "Его еще можно догнать");
        poll("Поискать Кота")
                .choice("Поискать Кота", this::goFind)
                .choice("Остаться на месте", this::dontGo)
                .send();
    }

    private void lookAround(ChooseContext chooseContext) {
        messages(
                "Отличный день для того, чтобы посидеть и поглазеть в окно.",
                "Но, подождите-ка.. кто это?",
                "Кошка вывела на прогулку маленького котенка. Вот это удача!");
        catchUpCatAndClose(CAT3);
    }

    private void goFind(ChooseContext chooseContext) {
        messages(
                "Радуясь прекрасной погоде вы идете по улице",
                "И тут вы видите впереди...",
                "Нет, это не кот, а полиция, вы нарушили режим самоизоляции");
        catchUpCatAndClose(NOT_CAT);
    }

    private void dontGo(ChooseContext chooseContext) {
        messages(
                "Вы стоите посреди улицы, вокруг шумят деревья, отбрасывая тень на соседние дома",
                "Из-за соседнего дома выбегает Кот и направляется по своим делам");
        catchUpCatAndClose(CAT1);
    }
}
