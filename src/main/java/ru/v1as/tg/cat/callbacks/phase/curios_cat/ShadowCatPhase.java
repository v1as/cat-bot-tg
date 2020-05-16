package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.EmojiConst.ATHLETIC_SHOE;
import static ru.v1as.tg.cat.EmojiConst.EYES;
import static ru.v1as.tg.cat.EmojiConst.GHOST;
import static ru.v1as.tg.cat.EmojiConst.GLASSES;
import static ru.v1as.tg.cat.EmojiConst.LIGHT_BULB;
import static ru.v1as.tg.cat.EmojiConst.NIGHT_CITY;
import static ru.v1as.tg.cat.EmojiConst.RAISED_HAND;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.TgInlinePoll;

/*
 * author: AnnaTemnaya
 * */
@Component
public class ShadowCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages(
                "На улице приятная погода, не жарко, не холодно, "
                        + "не дождливо, в самый раз для неспешной прогулки.",
                "Поэтому вы решили идти домой длинным путём.");
        poll("Куда пойдём?")
                .choice("По освещенной улице " + LIGHT_BULB, this::light)
                .choice("По тёмному переулку " + NIGHT_CITY, this::dark)
                .send();
    }

    private void light(ChooseContext chooseContext) {
        messages(
                "Повернув направо, вы движетесь прогулочным шагом вдоль домов.",
                "Тут дорогу вам перебегает Любопытная Тень Кота.");
        poll("Что делаем?")
                .choice("Идём за тенью " + GHOST, this::follow)
                .choice("Оглядываемся " + EYES, this::look)
                .send();
    }

    private void follow(ChooseContext chooseContext) {
        message("Вы сворачиваете на тропинку между деревьев.");
        poll("Где-то тут должен быть Кот.").choice("Приглядеться " + GLASSES, this::toPeer1).send();
    }

    private void toPeer1(ChooseContext chooseContext) {
        poll("Вы не можете рассмотреть этого чёрного проказника в темноте.")
                .choice("Приглядеться " + GLASSES, this::toPeer2)
                .send();
    }

    private void toPeer2(ChooseContext chooseContext) {
        poll("Вы очень стараетесь, но всё ещё не видите Кота.")
                .choice("Приглядеться " + GLASSES, random(this::toPeerSuccess, this::toPeerFail))
                .send();
    }

    private void toPeerSuccess(ChooseContext chooseContext) {
        messages("Наконец вы замечаете какое-то шевеление, и да, это Кот!");
        catchUpCatAndClose(CAT1);
    }

    private void toPeerFail(ChooseContext chooseContext) {
        messages("Что ж, вы очень старались, но не смогли найти затаившегося Кота");
        catchUpCatAndClose(NOT_CAT);
    }

    private void look(ChooseContext chooseContext) {
        final TgInlinePoll poll =
                poll(
                        "Вам лень идти в темноту кроны деревьев, где скрылся Кот, и вы решили осмотреться.");
        if (randomBool()) {
            poll.choice("Смотрим вправо " + EYES, this::right);
        } else {
            poll.choice("Смотрим влево " + EYES, this::left);
        }
        poll.send();
    }

    private void right(ChooseContext chooseContext) {
        messages(
                "Вы смотрите направо, в сторону, откуда прибежал Кот.",
                "Там сидит милая серая Кошечка.",
                "Видимо, Любопытный Кот убежал выполнять каприз своей дамы.");
        catchUpCatAndClose(CAT1);
    }

    private void left(ChooseContext chooseContext) {
        messages(
                "Вы смотрите налево, в сторону, куда убежал Кот.",
                "И замечаете малюсенького серенького котёночка,"
                        + " к которому и бросился наш Любопытный Кот, чтобы унести беглеца домой.");
        catchUpCatAndClose(CAT2);
    }

    private void dark(ChooseContext chooseContext) {
        messages(
                "Свернув с тротуара на земляную тропинку, вы идёте по мини-парку у частных домов.",
                "Тут вы слышите топот маленьких пушистых лапок.");
        poll("Что делаем?")
                .choice("Останавливаемся " + RAISED_HAND, this::stop)
                .choice("Продолжаем идти " + ATHLETIC_SHOE, this::keepFollow)
                .send();
    }

    private void stop(ChooseContext chooseContext) {
        messages(
                "Вы резко замираете на месте и, не дыша, прислушиваетесь.",
                "Теперь вы слышите, что топот где-то за вами.",
                "Вы осторожно оборачиваетесь и видите бегущего по своим делам Любопытного Кота.");
        catchUpCatAndClose(CAT1);
    }

    private void keepFollow(ChooseContext chooseContext) {
        messages(
                "Не сбавляя и так медленный шаг, вы спокойно продолжаете идти.",
                "Но топот всё отдаляется.",
                "И затихает совсем.",
                "Вам не повезло встретить сегодня Кота, но зато прогулялись вы славно.");
        catchUpCatAndClose(NOT_CAT);
    }
}
