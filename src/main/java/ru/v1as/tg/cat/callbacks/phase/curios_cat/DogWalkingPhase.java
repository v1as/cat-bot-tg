package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.lang.Math.max;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.fromAmount;

import java.time.Duration;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
 * author: AnnaTemnaya
 * */
@Component
public class DogWalkingPhase extends AbstractCuriosCatPhase {

    public static final String CATS_AMOUNT = "CATS_AMOUNT";

    @Override
    protected void open() {
        messages(
                "Вы вышли погулять с собакой.",
                "Вам предстоит выбрать свой дальнейший путь, в сторону леса вдоль семнадцатиэтажек"
                    + " или же через пустырь во дворы пятиэтажек.");
        poll("Куда пойдём?")
                .choice("К лесу \uD83C\uDF32", this::forest)
                .choice("К пятиэтажкам \uD83C\uDFE2", this::house5)
                .send();
    }

    private void forest(ChooseContext chooseContext) {
        messages(
                "Повернув к лесу, вы оглядываетесь вокруг в поисках, конечно же, Котов.",
                "Но ни у лоточков с их едой, ни на привычном их месте нет ни одного Кота.");

        poll("Куда дальше?")
                .choice("Во дворы \uD83C\uDFE2", this::yard)
                .choice("Вдоль гаражей \uD83D\uDE97", this::garage)
                .send();
    }

    private void yard(ChooseContext chooseContext) {
        messages(
                "Вы идёте по двору, но и тут нет Котов!",
                "Подняв взгляд на пролетающую мимо птицу, замечаете в окне мечтающего Кота.",
                "Ну хоть один, и то хорошо.");
        catchUpCatAndClose(CAT1);
    }

    private void garage(ChooseContext chooseContext) {
        messages(
                "Собака спокойно бегает на длинном поводке.",
                "По пути в лес вы не встречаете ни машин, ни других собак, ни тем более Кота.",
                "Подождите-ка, это что, Кот?",
                "Ой, нет, это всего лишь его рисунок на чьём-то гараже.",
                "Интересно, зачтётся ли рисунок за живого Кота?");
        randomWay(chooseContext, c -> catchUpCatAndClose(CAT1), c -> catchUpCatAndClose(NOT_CAT));
    }

    private void house5(ChooseContext chooseContext) {
        messages("Вы сворачиваете на пустырь.");
        poll("Вы знаете, что тут живёт один Кот, и пытаетесь его высмотреть.")
                .choice(CAT + " Кот!", this::suddenlyCat)
                .timeout(
                        new PollTimeoutConfiguration(Duration.ofMillis(1000))
                                .onTimeout(() -> notSuddenlyCat(chooseContext)))
                .send();
    }

    private void suddenlyCat(ChooseContext chooseContext) {
        message("Вы заметили своего знакомого Кота на дереве, неплохо.");
        getPhaseContext().increment(CATS_AMOUNT);
        house5Continuous(chooseContext);
    }

    private void notSuddenlyCat(ChooseContext chooseContext) {
        message("Кот где-то мяукнул, но вы не успели среагировать на звук, и Кот скрылся.");
        house5Continuous(chooseContext);
    }

    private void house5Continuous(ChooseContext chooseContext) {
        messages(
                "Идёте дальше вдоль старенькой пятиэтажки.",
                "Впереди стоят в ряд несколько кормушек Котов.",
                "Издалека не видно, есть ли там сами Коты.");
        poll("Что делаем?")
                .choice("Подходим ближе по прямой", this::toFeeder)
                .choice("Обходим кормушки по кругу", this::aroundFeeder)
                .send();
    }

    private void toFeeder(ChooseContext chooseContext) {
        messages(
                "Вы смело топаете вперёд.",
                "Ваша собака замечает Котов впереди раньше вас и дёргает поводок.");

        poll("Что делаем?")
                .choice("Окрикнуть пса \uD83E\uDDAE", this::shoutDog)
                .choice("Молча дёрнуть поводок \uD83D\uDE10", this::silenceControlDog)
                .send();
    }

    private void shoutDog(ChooseContext chooseContext) {
        messages(
                "Вы громко кричите \"Ко мне!\", чем пугаете не только свою собаку,"
                        + " птиц и случайного прохожего, но и всех Котов.",
                "Они разбегаются, кто куда.",
                "Как же много их было.");
        switch (random(NOT_CAT, CAT1, CAT2)) {
            case NOT_CAT:
                messages("И, к сожалению, вы не успели рассмотреть ни одного");
                break;
            case CAT1:
                messages("Но вы всё-таки успеваете рассмотреть одного");
                getPhaseContext().increment(CATS_AMOUNT);
                break;
            case CAT2:
                messages("Но вы всё-таки успеваете рассмотреть парочку");
                getPhaseContext().increment(CATS_AMOUNT, 2);
                break;
        }
        countCatsAmount();
    }

    private void countCatsAmount() {
        final int catsAmount = max(getPhaseContext().get(CATS_AMOUNT, 0), 3);
        catchUpCatAndClose(fromAmount(catsAmount));
    }

    private void silenceControlDog(ChooseContext chooseContext) {
        messages(
                "Вы сильно тянете поводок на себя, и собака послушно возвращается.",
                "Коты, готовые было убежать, спокойно садятся на места.",
                "Вам повезло!");
        getPhaseContext().increment(CATS_AMOUNT, 3);
        countCatsAmount();
    }

    private void aroundFeeder(ChooseContext chooseContext) {
        messages(
                "Вы осторожно идёте по большой дуге вокруг кормушек, придерживая пса.",
                "Медленно и тихо вы подошли с другой стороны к ним и можете рассмотреть, сколько тут Котов.");
        getPhaseContext().increment(CATS_AMOUNT, random(1, 2));
        countCatsAmount();
    }
}
