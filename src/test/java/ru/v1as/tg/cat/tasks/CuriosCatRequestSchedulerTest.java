package ru.v1as.tg.cat.tasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.CaBotTestConfiguration;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.service.clock.TestBotClock;
import ru.v1as.tg.cat.tg.TgSender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
public class CuriosCatRequestSchedulerTest extends AbstractCatBotTest {

    @Autowired TestBotClock clock;
    @Autowired CatBotData catBotData;
    @Autowired TgSender sender;
    @Autowired JoinCatFollowPhase joinCatPhase;
    CuriosCatRequestScheduler scheduler;
    @Autowired ChatDao chatDao;

    @Before
    public void init() {
        scheduler = new CuriosCatRequestScheduler(joinCatPhase, chatDao);
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

        popSendMessage().assertText("Любопытный Кот убежал к @User0   \uD83D\uDC08");
        popDeleteMessage();
    }

    @Test
    public void curiosCatGoneTest() {
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        scheduler.setChance(1);
        scheduler.run();

        ArgumentCaptor<Runnable> closeRequests = ArgumentCaptor.forClass(Runnable.class);
        verify(executor, times(2)).schedule(closeRequests.capture(), anyLong(), any());
        closeRequests.getAllValues().get(1).run();

        popSendMessage().assertText("Любопытный кот гуляет рядом");
        popDeleteMessage();
    }
}
