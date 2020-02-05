package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

import org.springframework.stereotype.Component;

@Component
public class JustOneCatPhase extends AbstractCuriosCatPhase {

    @Override
    protected void open() {
        messages("Вам засчитан кот.");
        catchUpCatAndClose(CAT1);
    }

}
