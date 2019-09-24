package ru.v1as.tg.cat.tasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
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
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
public class CuriosCatRequestSchedulerTest extends AbstractCatBotTest {

    @Autowired CatBotData catBotData;
    @Autowired UnsafeAbsSender sender;
    CuriosCatRequestScheduler scheduler;

    @Before
    public void init() {
        scheduler = new CuriosCatRequestScheduler(catBotData, sender);
        scheduler.setExecutorService(mock(ScheduledExecutorService.class));
        scheduler.init();

        getCatBotData().register(getMessageUpdate());
    }

    @Test
    public void curiosCatSuccessTest() {
        scheduler.setChance(1);
        scheduler.setFirstTime(false);
        scheduler.run();

        popSendMessage("Любопытный кот гуляет рядом");
        sendCallback(lastMsgId, "curiosCat");
        popSendMessage("Любопытный Кот убежал к @User0   \uD83D\uDC08");
        popDeleteMessage();
    }

    @Test
    public void curiosCatGoneTest() {
        ScheduledExecutorService executor = mock(ScheduledExecutorService.class);
        scheduler.setExecutorService(executor);
        scheduler.setChance(1);
        scheduler.run();

        ArgumentCaptor<Runnable> closeRequests = ArgumentCaptor.forClass(Runnable.class);
        verify(executor, times(2)).schedule(closeRequests.capture(), anyLong(), any());
        closeRequests.getAllValues().get(1).run();

        popSendMessage("Любопытный кот гуляет рядом");
        popDeleteMessage();
    }
}
