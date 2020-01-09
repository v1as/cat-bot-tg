package ru.v1as.tg.cat.tasks;

import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.CaBotTestConfiguration;
import ru.v1as.tg.cat.service.clock.TestBotClock;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
public class CuriosCatRequestSchedulerTest extends AbstractCatBotTest {

    @Autowired TestBotClock clock;
    @Autowired CuriosCatRequestScheduler scheduler;

    @Before
    public void init() {
        scheduler.init();
        bob.inPublic().sendCommand("/enable_polls");
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

        public0.getEditMessage().assertContainText("не пойдёт");
        clock.skip(11, TimeUnit.SECONDS);

        public0.getSendMessage().assertContainText("Любопытный Кот убежал к @bob");
        public0.getDeleteMessage();
    }

    @Test
    public void curiosCatGoneTest() {
        scheduler.setChance(1);
        scheduler.run();

        public0.getSendMessage().assertText("Любопытный Кот гуляет рядом");
        clock.skip(5 + 1, TimeUnit.MINUTES);

        public0.getDeleteMessage();
    }
}
