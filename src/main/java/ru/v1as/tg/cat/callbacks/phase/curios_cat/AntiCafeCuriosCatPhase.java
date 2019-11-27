package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.SMIRK_CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
public class AntiCafeCuriosCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "Сегодня на улице ветрено и довольно неуютно.",
                "Пушистый хвост впереди не даёт вам сбавить шаг.",
                "Похоже, Любопытный Кот вас сегодня куда-то ведёт.");
        poll("Что делаем?")
                .choice("Нагнать кота", this::goFaster)
                .choice("Не менять темп", this::justGo)
                .send();
    }

    private void justGo(ChooseContext chooseContext) {
        messages(
                "Вы так и идёте за Котом, не меняя темпа.",
                "Кошак, не оборачиваясь, бежит впереди.");
        cafe(chooseContext);
    }

    private void goFaster(ChooseContext chooseContext) {
        messages(
                "Вы попытались догнать кота, но тот, не оборачиваясь, прибавил шагу.",
                "Дистанция между вами не сократилась");
        cafe(chooseContext);
    }

    private void cafe(ChooseContext chooseContext) {
        messages(
                "Похоже, цель вашей прогулки близка.",
                "Вы приближаетесь к трехэтажному кирпичному зданию.",
                "Кот, не останавливаясь, забегает в приоткрытую дверь.");
        poll("Как поступим?")
                .choice("Пойти за котом", this::goInto)
                .choice("Оглядеться", this::lookAround)
                .send();
    }

    private void goInto(ChooseContext chooseContext) {
        messages(
                "Вы заходите в здание.",
                "На первый взгляд это какой-то ресторанчик.",
                "А, нет. Это кафе.",
                "Анти-кафе.",
                "Это кошачье анти-кафе!",
                "К вашим ногам подбегает сразу несколько незнакомых кошек.",
                "Как жаль, что вы находитесь внутри здания, и вам их никто не засчитает.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void lookAround(ChooseContext chooseContext) {
        messages(
                "Дверь за котом закрывается.",
                "Вы внимательно изучаете окружение...",
                "Над дверью, за которой исчез кот, висит надпись 'В гостях у Мурки'.");
        poll("Что делаем?")
                .choice("Зайти внутрь", this::goInto2)
                .choice("Остаться снаружи", this::lookAround2)
                .send();
    }

    private void goInto2(ChooseContext chooseContext) {
        messages(
                "Вы заходите в здание.",
                "К вашим ногам подбегает сразу несколько незнакомых кошек.",
                "Как жаль, что вы находитесь внутри здания, и вам их никто не засчитает.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void lookAround2(ChooseContext chooseContext) {
        final CatRequestVote cat = random(CAT1, CAT2, CAT3);
        message("Вы стоите и рассматриваете окна здания.");
        if (CAT3.equals(cat)) {
            message("Одного за другим вы замечаете трёх котов в окнах.");
        } else if (CAT2.equals(cat)) {
            message("Сначала одного, а потом и второго, вы замечаете котов в окне.");
        } else if (CAT1.equals(cat)) {
            messages(
                    "Больше ничего интересного вы не замечаете.",
                    "Пока вдруг в одном из окон не появляется довольная мордочка Любопытного Кота.",
                    "Этот наглец вам подмигнул " + SMIRK_CAT);
        }
        catchUpCatAndClose(cat);
    }
}
