package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static java.time.temporal.ChronoUnit.SECONDS;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT3;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import java.time.Duration;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

/*
   author: AshaOwl
*/
@Component
public class HuntingCatPhase extends AbstractCuriosCatPhase {

    protected final PollTimeoutConfiguration SHORT_WAITING =
            new PollTimeoutConfiguration(Duration.of(3, SECONDS))
                    .removeMsg(true)
                    .onTimeout(this::shortWaiting);

    protected final PollTimeoutConfiguration LONG_WAITING =
            new PollTimeoutConfiguration(Duration.of(6, SECONDS))
                    .removeMsg(true)
                    .onTimeout(this::longWaiting);

    protected final PollTimeoutConfiguration FINISH_WAITING =
            new PollTimeoutConfiguration(Duration.of(12, SECONDS))
                    .removeMsg(true)
                    .onTimeout(this::finishWaiting);

    @Override
    protected void open() {
        messages(
                "В этот раз Кот промчался мимо и пропал из виду.",
                "После некоторого времени блуждания, вы вновь натыкаетесь на Кота.",
                "Кот чрезвычайно занят, он затаился в кустах и охотится на птиц.",
                "Скорее всего, кот убежит, если его потревожить.",
                "С выбором дальнейших действий не стоит спешить.");

        poll("Что дальше?")
                .choice("Кот!", this::interruptedHunt)
                .choice("Подойти поближе", this::noCat)
                .timeout(random(SHORT_WAITING, LONG_WAITING))
                .send();
    }

    private void noCat(ChooseContext ctx) {
        messages(
                "Неосторожное приближение спугнуло кота.",
                "Кот убежал, а вы не успели его сосчитать.");
        catchUpCatAndClose(NOT_CAT);
    }

    private void interruptedHunt(ChooseContext ctx) {
        // кот недоволен, но посчитан
        messages(
                "От вашего восклицания птицы испугались и улетели.",
                "Вы посчитали кота, но помешали ему охотиться.",
                "Как долго вы будете помнить осуждающий взгляд Любопытного Кота?");
        catchUpCatAndClose(CAT1);
    }

    private void shortWaiting() {
        messages("Ожидание привело к изменениям.", "Кот выбрал жертву и уверенно к ней крадётся.");
        poll("Пора выбрать следующее действие?")
                .choice("Кот!", this::twoHunters)
                .choice("Подойти поближе", this::noCat)
                .timeout(LONG_WAITING)
                .send();
    }

    private void twoHunters(ChooseContext ctx) {
        messages(
                "Кот вас услышал и остановился, оглядываясь.",
                "Внезапно вы замечаете, как с другой стороны крадётся ещё один кот!",
                "Появился ещё один кот-охотник.");
        poll("Что дальше?")
                .choice("Ещё один кот!", this::twoCats)
                .choice("Наблюдать издалека", this::justWatching)
                .send();
    }

    private void justWatching(ChooseContext ctx) {
        messages(
                "Второй кот оказывается решительнее.",
                "Он бросается на ближайшую птицу, хватает её и скрывается в кустах.",
                "Ну, хотя бы одного кота вы успели сосчитать.");
        catchUpCatAndClose(CAT1);
    }

    private void twoCats(ChooseContext ctx) {
        messages(
                "От вашего восклицания птицы испугались и улетели.",
                "Вы посчитали обоих котов, но помешали им охотиться.");
        catchUpCatAndClose(CAT2);
    }

    private void longWaiting() {
        message("Что-то изменилось.");
        poll("Что же дальше?")
                .choice("Кот!", this::threeHunters)
                .choice("Подойти поближе", this::noCat)
                .timeout(FINISH_WAITING)
                .send();
    }

    private void threeHunters(ChooseContext ctx) {
        messages(
                "Кот вас услышал и остановился, оглядываясь.",
                "Внезапно вы замечаете, как с другой стороны крадётся ещё один кот.",
                "Но всех опередил ещё один кот-охотник, выскочив вперёд в толпу птиц!",
                "Птицы разлетелись, все три кота остались ни с чем.",
                "Конечно, вы сосчитали их всех.");
        catchUpCatAndClose(CAT3);
    }

    private void finishWaiting() {
        messages(
                "Последний раз вы видели кота, когда он перебегал к кусту поближе к птицам.",
                "Но вы ждёте слишком долго, а кот так и не появляется...",
                "Ожидание затянулось, и вы решили подойти проверить тот куст.",
                "Кота там нет.",
                "Похоже, пока вы ожидали, кот внезапно потерял интерес к охоте и куда-то ушёл."
                );
        catchUpCatAndClose(NOT_CAT);
    }
}
