package ru.v1as.tg.cat.callbacks.phase.impl;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.RegattaDreamPhaseTest.CuriosConfiguration;
import ru.v1as.tg.cat.model.random.RandomItem;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Import(CuriosConfiguration.class)
public class JoinCatFollowPhaseTest extends AbstractCatBotTest {

    @Autowired private JoinCatFollowPhase phase;
    @Autowired private FixedCatQuestProducer catQuestProducer;
    @Autowired private JustOneCatPhase justOneCatPhase;

    @Before
    public void before() {
        super.before();
        catQuestProducer.set(justOneCatPhase);
    }

    @Test
    public void userShouldFollowTheCat() {
        phase.open(wrap(inPublic.getChat()));

        bob.inPublic()
                .getSendMessageToSend()
                .assertContainText("Любопытный Кот")
                .findCallbackToSend("Пойти за котом")
                .sendStart();

        inPublic.getDeleteMessage().assertTextContains("Любопытный Кот гуляет рядом");

        bob.inPrivate().getSendMessage().assertText("Вам засчитан кот.");
        inPublic.getSendMessage().assertContainText("Любопытный кот убегает к @bob");
    }

    private AbstractCuriosCatPhase choose(RandomRequest<AbstractCuriosCatPhase> randomRequest) {
        return randomRequest.getItems().stream()
                .map(RandomItem::getValue)
                .filter(q -> q.getName().equals("JustOneCatPhase"))
                .findFirst()
                .orElseThrow(RuntimeException::new);
    }
}
