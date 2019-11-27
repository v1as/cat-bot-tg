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
        sendCommand("/enable_polls");
        clearMethodsQueue();
    }

    @Test
    public void curiosCatSuccessTest() {
        scheduler.setChance(1);
        scheduler.setFirstTime(false);
        scheduler.run();

        popSendMessage().assertText("Любопытный кот гуляет рядом").findCallback("Кот").send();
        popEditMessage().assertContainText("не пойдёт");
        clock.skip(11, TimeUnit.SECONDS);

        popSendMessage().assertText("Любопытный Кот убежал к @User0 \uD83D\uDC08");
        popDeleteMessage();
    }

    @Test
    public void curiosCatGoneTest() {
        scheduler.setChance(1);
        scheduler.run();

        popSendMessage().assertText("Любопытный кот гуляет рядом");
        clock.skip(5 + 1, TimeUnit.MINUTES);

        popDeleteMessage();
    }
}
