package ru.v1as.tg.cat.tasks;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ScheduledExecutorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class CuriosCatRequestSchedulerTest extends AbstractCatBotTest {

    @Before
    public void init() {
        getCatBotData().register(getMessageUpdate());
    }

    @Test
    public void curiosCatSuccessTest() {
        CuriosCatRequestScheduler scheduler =
                new CuriosCatRequestScheduler(
                        mock(ScheduledExecutorService.class), getCatBotData(), bot);
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

        CuriosCatRequestScheduler scheduler =
                new CuriosCatRequestScheduler(executor, getCatBotData(), bot);
        scheduler.setChance(1);
        scheduler.run();

        ArgumentCaptor<Runnable> closeRequests = ArgumentCaptor.forClass(Runnable.class);
        verify(executor, times(3)).schedule(closeRequests.capture(), anyLong(), any());
        closeRequests.getAllValues().get(2).run();

        popSendMessage("Любопытный кот гуляет рядом");
        popDeleteMessage();
    }
}
