package ru.v1as.tg.cat.callbacks.phase.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
@Primary
@Profile("test")
@RequiredArgsConstructor
public class FixedCatQuestProducer implements CuriosCatQuestProducer {

    private AbstractCuriosCatPhase phase;

    @Override
    public AbstractCuriosCatPhase get(TgUser user, TgChat chat) {
        return phase;
    }

    public void set(AbstractCuriosCatPhase phase) {
        this.phase = phase;
    }
}
