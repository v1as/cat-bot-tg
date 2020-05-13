package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: AnnaTemnaya
 * */
@Component
public class ThunderCatsPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "На улице дует сильный ветер.",
                "Вдалеке собираются чёрные тучи и слышен гром.",
                "Вам надо выйти на улицу по делам, но погода не располагает.");

        poll("Что делаем?")
                .choice("Идём на улицу \uD83C\uDF33", this::street)
                .choice("Сидим дома \uD83C\uDFE1", this::home)
                .send();
    }

    private void street(ChooseContext chooseContext) {
        messages(
                "Вы решили, что успеете до грозы сбегать по делам.",
                "Выйдя из подъезда, понимаете, что забыли зонт, а мелкий дождик уже начинается.");
        poll("Что делаем?")
                .choice("Возвращаемся за зонтом ⛱", this::umbrella)
                .choice("Быстро идём \uD83D\uDC5F", this::run)
                .send();
    }

    private void umbrella(ChooseContext chooseContext) {
        messages(
                "Вы заходите домой и долго ищете зонт, за это время дождь усилился в несколько раз.",
                "Но, потратив столько усилий на сборы, вы героически решили всё же идти по делам.",
                "Вы выходите из подъезда и видите у крыльца на всё уменьшающемся сухом пятачке кошку и котенка,"
                        + " прячущихся от дождя.",
                "Вздохнув, дарите им свой зонт и возвращаетесь домой.");
        catchUpCatAndClose(CAT3);
    }

    private void run(ChooseContext chooseContext) {
        messages(
                "За зонтом идти лень, и вы быстро идёте, почти бежите, в нужную вам сторону.",
                "Вы не забываете оглядываться в поисках котов, но они все заранее спрятались.",
                "Уже на обратном пути, под проливным дождём вы бежите домой, почти забежали в свой подъезд,"
                        + " но вдруг видите мелькнувший хвост Кота впереди.");

        poll("Что делаем?")
                .choice("За котом \uD83D\uDC08", this::runForCat)
                .choice("Нырнуть в подъезд \uD83C\uDFE2", this::hideInPorch)
                .send();
    }

    private void runForCat(ChooseContext ctx) {
        messages(
                "Ещё быстрее вы устремляетесь в сторону Кота.",
                "На бегу оглядываетесь в его поисках.");
        randomWay(ctx, this::runForCatSuccess, this::runForCatFailure);
    }

    private void runForCatSuccess(ChooseContext chooseContext) {
        messages(
                "Вот он! Вам повезло, вы заметили в последний момент, как Кот залезает в"
                        + " окно подвала.");
        catchUpCatAndClose(CAT1);
    }

    private void runForCatFailure(ChooseContext chooseContext) {
        messages("Но его нигде нет и, промокнув до нитки, вы идёте домой.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void hideInPorch(ChooseContext ctx) {
        messages(
                "Вы быстрее прячетесь под крышей подъезда.",
                "Кот убежал и возвращаться не думает.");
        randomWay(ctx, this::hideInPorchSuccess, this::hideInPorchFailure);
    }

    private void hideInPorchSuccess(ChooseContext chooseContext) {
        messages(
                "В последний раз взглянув в сторону угла дома, где скрылся Кот, вы замечаете"
                        + " его мордочку в подвальном окошке.");
        catchUpCatAndClose(CAT1);
    }

    private void hideInPorchFailure(ChooseContext chooseContext) {
        messages("Что ж, по крайней мере, вы сделали свои дела, хоть в чём-то повезло.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void home(ChooseContext ctx) {
        messages(
                "Поленившись куда-то идти, вы решили подышать свежим предгрозовым воздухом с балкона.",
                "Разглядываете приближающиеся тучи, смотрите по сторонам.",
                "И тут замечаете какое-то движение во дворе.",
                "Это коты разбегаются по своим жилищам, ища спасения от начавшегося дождя.");

        poll("Что делаем?")
                .choice(
                        "Считаем котов \uD83D\uDCF8",
                        c -> randomWay(c, this::countCatsSuccess, this::countCatsFailure))
                .choice("Кот! \uD83D\uDC08", (c -> this.catchUpCatAndClose(CAT1)))
                .send();
    }

    private void countCatsSuccess(ChooseContext chooseContext) {
        messages("Раз, два, три - столько котов вы успели посчитать!");
        catchUpCatAndClose(CAT3);
    }

    private void countCatsFailure(ChooseContext chooseContext) {
        messages(
                "Котов очень много, и они очень быстрые, аж глаза разбегаются.",
                "Вы пытаетесь рассмотреть внимательнее хотя бы одного.",
                "Но все они - сплошные расплывчатые пятна.");
        catchUpCatAndClose(NOT_CAT);
    }
}
