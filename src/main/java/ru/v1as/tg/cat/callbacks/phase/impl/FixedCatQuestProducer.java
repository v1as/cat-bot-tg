package ru.v1as.tg.cat.callbacks.phase.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;

@Component
@Primary
@Profile("test")
@RequiredArgsConstructor
public class FixedCatQuestProducer implements CuriosCatQuestProducer {

    private AbstractCuriosCatPhase phase;

    @Override
    public AbstractCuriosCatPhase get(Integer userId) {
        return phase;
    }

    public void set(AbstractCuriosCatPhase phase) {
        this.phase = phase;
    }
}
