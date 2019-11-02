package ru.v1as.tg.cat.commands.impl;

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
        timeout(1000);
        message("Ваш общий с котом путь лежит близ жилых домов.");
        timeout(2000);
        message("Проходя мимо одного из них, вы едва различает тихий писк.");
        poll("Что будем делать?")
                .choice("Следовать за котом", random(this::keepFollow, this::keepFollowFail))
                .choice("Проверить писк", random(this::checkSound, this::checkSoundFail))
                .send();
    }

    private void checkSound(ChooseContext ctx) {
        timeout(2000);
        message(
                "Подойдя к подъезду, вы обнаруживаете в подвальной нише коробку, в которой сидит кошка с котёнком.");
        timeout(2000);
        catchUpCatAndClose(ctx, CatRequestVote.CAT3);
    }

    private void checkSoundFail(ChooseContext ctx) {
        timeout(2000);
        message(
                "Подойдя к подъезду, вы заглядываете в подвальную нишу, из которой доноится подозрительный звук.");
        timeout(1000);
        message("Снизу выныривает испуганная крыса.");
        timeout(1000);
        message("Само-собой уже и след простыл.");
        catchUpCatAndClose(ctx, NOT_CAT);
    }

    private void keepFollow(ChooseContext ctx) {
        timeout(1000);
        message("Не обращая внимания на писк, вы уверенно продолжаете следовать за котом.");
        timeout(1000);
        catchUpCatAndClose(ctx, CatRequestVote.CAT1);
    }

    private void keepFollowFail(ChooseContext ctx) {
        timeout(1000);
        message(
                "Даже мимолётного отвлечения на странный звук хватило паршивцу чтобы сбежать."
                        + " Ничего, в следующий раз может и повезти.");
        timeout(1000);
        catchUpCatAndClose(ctx, NOT_CAT);
    }
}
