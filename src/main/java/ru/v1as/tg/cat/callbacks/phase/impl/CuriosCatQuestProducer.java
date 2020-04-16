package ru.v1as.tg.cat.callbacks.phase.impl;

import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;

public interface CuriosCatQuestProducer {

    AbstractCuriosCatPhase get(Integer userId);
}
