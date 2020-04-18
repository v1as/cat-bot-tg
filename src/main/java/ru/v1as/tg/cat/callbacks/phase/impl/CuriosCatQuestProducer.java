package ru.v1as.tg.cat.callbacks.phase.impl;

import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface CuriosCatQuestProducer {

    AbstractCuriosCatPhase get(TgUser user, TgChat chat);
}
