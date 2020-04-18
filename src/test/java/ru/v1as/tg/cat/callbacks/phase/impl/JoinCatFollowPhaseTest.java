package ru.v1as.tg.cat.callbacks.phase.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.service.ChatParamResource;

@Import(CuriosConfiguration.class)
public class JoinCatFollowPhaseTest extends AbstractCatBotTest {

    @Autowired private JoinCatFollowPhase phase;
    @Autowired private FixedCatQuestProducer catQuestProducer;
    @Autowired private JustOneCatPhase justOneCatPhase;
    @Autowired private ChatParamResource paramResource;

    @Before
    public void before() {
        super.before();
        catQuestProducer.set(justOneCatPhase);
    }

    @Test
    public void userShouldFollowTheCat() {
        assertEquals(0, paramResource.paramInt(inPublic.getId(), bob.getUserId(), MONEY));

        phase.open(wrap(inPublic.getChat()));

        followTheCat();

        bob.inPrivate().getSendMessage().assertText("Вам засчитан кот.");
        inPublic.getSendMessage().assertContainText("Любопытный кот убегает к @bob");

        assertEquals(3, paramResource.paramInt(inPublic.getId(), bob.getUserId(), MONEY));
    }

    private void followTheCat() {
        bob.inPublic()
            .getSendMessageToSend()
            .assertContainText("Любопытный Кот")
            .findCallbackToSend("Пойти за котом")
            .sendStart();

        inPublic.getDeleteMessage().assertTextContains("Любопытный Кот гуляет рядом");
    }

    @Test
    public void userShouldFollowTheCatConcentrationPotion() {
        assertEquals(0, paramResource.paramInt(inPublic.getId(), bob.getUserId(), MONEY));
        paramResource.param(inPublic.getId(), bob.getUserId(), CONCENTRATION_POTION, "true");

        phase.open(wrap(inPublic.getChat()));

        followTheCat();

        bob.inPrivate().getSendMessage().assertText("Вам засчитан кот.");
        inPublic.getSendMessage()
                .assertContainText("Два кота засчитано игроку @bob");

        assertEquals(6, paramResource.paramInt(inPublic.getId(), bob.getUserId(), MONEY));
    }

}
