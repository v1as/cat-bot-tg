package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;

@Component
@RequiredArgsConstructor
public class KittenOrRatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        message("Ваш общий с котом путь лежит близ жилых домов.");
        message("Проходя мимо одного из них, вы слышите едва различает тихий писк.");
        poll("Что будем делать?")
                .choice("Следовать за котом", random(this::keepFollow, this::keepFollowFail))
                .choice("Проверить писк", random(this::checkSound, this::checkSoundFail))
                .send();
    }

    private void checkSound(ChooseContext ctx) {
        message(
                "Подойдя к подъезду, вы обнаруживаете в подвальной нише коробку, в которой сидит кошка с котёнком.");
        catchUpCatAndClose(ctx, CatRequestVote.CAT3);
    }

    private void checkSoundFail(ChooseContext ctx) {
        message(
                "Подойдя к подъезду, вы заглядываете в подвальную нишу, из которой доносится подозрительный звук.");
        message("Снизу выныривает испуганная крыса.");
        message("Само собой кота уже и след простыл.");
        catchUpCatAndClose(ctx, NOT_CAT);
    }

    private void keepFollow(ChooseContext ctx) {
        message("Не обращая внимания на писк, вы уверенно продолжаете следовать за котом.");
        catchUpCatAndClose(ctx, CatRequestVote.CAT1);
    }

    private void keepFollowFail(ChooseContext ctx) {
        message(
                "Даже мимолётного отвлечения на странный звук хватило паршивцу чтобы сбежать."
                        + " Ничего, в следующий раз может и повезти.");
        catchUpCatAndClose(ctx, NOT_CAT);
    }
}
