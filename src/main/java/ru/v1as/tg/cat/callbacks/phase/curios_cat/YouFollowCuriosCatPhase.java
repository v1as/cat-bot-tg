package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import java.util.function.Consumer;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class YouFollowCuriosCatPhase extends AbstractCuriosCatPhase {

    private static final String STANDING = "STANDING";
    private static final String WALKING = "WALKING";

    @Override
    protected void open() {
        messages(
                "Любопытный Кот сегодня чем-то недоволен.",
                "Немного покружив, он садится у вас за спиной.");
        poll("Что делаем?")
                .choice("Подойти к коту", this::comeToCat)
                .choice("Остаться стоять", this::stayStand)
                .send();
    }

    private void stayStand(ChooseContext choice) {
        final CuriosCatContext ctx = getPhaseContext();
        final Integer walking = ctx.get(WALKING, 0);
        final Integer standing = ctx.increment(STANDING);
        if (standing > 3) {
            messages(
                    "Коту наскучило и он отправился по своим делам.",
                    "Вы и дальше остались стоять на месте.",
                    "Почему же вам не засчитали этого кота?");
            catchUpCatAndClose(NOT_CAT);
        } else {
            messages("Вы стоите.", "Кот сидит.", "Ничего больше не происходит.");
            final String walkDesc = walking > 0 ? "Идти дальше" : "Уйти";
            poll("Что дальше?")
                    .choice("Продолжаем стоять", this::stayStand)
                    .choice("Подойти к коту", this::comeToCat)
                    .choice(walkDesc, this::walkAway)
                    .send();
        }
    }

    private void comeToCat(ChooseContext ctx) {
        messages(
                "Кот игриво прыгает вокруг вас.",
                "Наконец вам надоедает пытаться подойти к нему, и вы останавливаетесь.");
        stayStand(ctx);
    }

    private void walkAway(ChooseContext chooseContext) {
        final Integer walking = getPhaseContext().increment(WALKING);
        if (walking > 5) {
            boringWalk();
        } else {
            messages(
                    "Немного растерянно, вы начинаете идти, переодически оборачиваясь посмотреть на кота.",
                    "Кот, как ни в чем не бывало, следует за вами.",
                    "Сегодня вы устраиваете прогулку для Кота, а не наоборот.");
            poll("Что дальше?")
                    .choice("Продолжаем идти", this::keepGo)
                    .choice("Остановиться", this::stayStand)
                    .send();
        }
    }

    private void boringWalk() {
        messages(
                "Это была хоть и невероятно скучная, но всё же прогулка.",
                "Кот не разочарован, но и не доволен.");
        catchUpCatAndClose(CAT1);
    }

    private void keepGo(ChooseContext chooseContext) {
        final Integer walking = getPhaseContext().increment(WALKING);
        if (walking > 5) {
            boringWalk();
        } else {
            messages("Вы продолжаете идти.", "Кот следует за вами.");
            poll("Куда пойдём")
                    .choice("В парк", this::walkToThePark)
                    .choice("В лес", this::goToTheForest)
                    .choice("Просто идти дальше", this::keepGo)
                    .choice("Остановиться", this::stayStand)
                    .send();
        }
    }

    private void goToTheForest(ChooseContext chooseContext) {
        messages(
                "Сохраняя порядок, вы впереди, кот сзади, вы заходите в лес.",
                "Похоже кот тут нечастый гость.",
                "Его пушистая голова любопытно вертится из стороны в сторону.",
                "В этот раз вам удалось удивить кота, а сейчас ему пора по делам.");
        catchUpCatAndClose(CAT1);
    }

    private void walkToThePark(ChooseContext chooseContext) {
        messages("Сохраняя порядок, вы впереди, кот сзади, вы заходите в парк.");
        Consumer<ChooseContext> method = random(this::cats, this::emptyPark);
        method.accept(chooseContext);
    }

    private void cats(ChooseContext chooseContext) {
        messages(
                "О, это оказался неплохой выбор.",
                "В парке вы замечаете пару кошек.",
                "Пока вы их считаете, Любопытный Кот убегает.");
        catchUpCatAndClose(CAT2);
    }

    private void emptyPark(ChooseContext chooseContext) {
        messages(
                "Ничего интересного в парке не происходит.",
                "Впрочем кот не выглядит разочарованным - мяукнув вам на прощание, он убегает.");
        catchUpCatAndClose(CAT1);
    }
}
