package ru.v1as.tg.cat.tasks;

import static org.junit.Assert.assertTrue;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.CaBotTestConfiguration;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.service.ChatParam;
import ru.v1as.tg.cat.service.ChatParamResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
public class CuriosCatRequestSchedulerTest extends AbstractCatBotTest {

    @Autowired CuriosCatRequestScheduler scheduler;
    @Autowired ChatDetailsDao chatDetailsDao;
    @Autowired ChatParamResource chatParamResource;

    @Before
    public void init() {
        chatDetailsDao.findByChatId(inPublic.getId()).setEnabled(true);
        scheduler.init();
        chatParamResource.param(inPublic.getId(), bob.getUserId(), ChatParam.PICTURE_POLL_ENABLED, true);
        clearMethodsQueue();
    }

    @Test
    public void curiosCatSuccessTest() {
        scheduler.setChance(1);
        scheduler.setFirstTime(false);
        scheduler.run();

        bob.inPublic()
                .getSendMessageToSend()
                .assertText("Любопытный Кот гуляет рядом")
                .findCallbackToSend("Кот")
                .send();

        inPublic.getEditMessage().assertContainText("не пойдёт");
        clock.skip(11, TimeUnit.SECONDS);

        inPublic.getSendMessage().assertContainText("Любопытный Кот убегает к @bob");
        inPublic.getDeleteMessage();
    }

    @Test
    public void curiosCatGoneTest() {
        scheduler.setChance(1);
        scheduler.run();

        inPublic.getSendMessage().assertText("Любопытный Кот гуляет рядом");
        clock.skip(5 + 1, TimeUnit.MINUTES);

        inPublic.getDeleteMessage();
    }

    @Test
    public void chanceTest() {
        scheduler.setChance(0.2);
        assertTrue(0.8 - scheduler.increaseChance(5) < 0.0001);
        assertTrue(scheduler.increaseChance(0) < 0.0001);
    }
}
