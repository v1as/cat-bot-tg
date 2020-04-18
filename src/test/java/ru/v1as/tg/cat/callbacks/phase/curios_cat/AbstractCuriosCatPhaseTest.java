package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase.CuriosCatContext;
import ru.v1as.tg.cat.model.TgUserWrapper;
import ru.v1as.tg.cat.tg.TestUser;

public abstract class AbstractCuriosCatPhaseTest extends AbstractCatBotTest {

    CuriosCatContext getStartCtx(TestUser testUser) {
        final CuriosCatContext phaseContext =
                new CuriosCatContext(
                        wrap(testUser.getPrivateChat().getChat()),
                        wrap(inPublic.getChat()),
                        TgUserWrapper.wrap(testUser.getUser()),
                        testUser.inPublic().sendTextMessage("Starting!"));
        clearMethodsQueue();
        return phaseContext;
    }
}
