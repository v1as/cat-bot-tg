package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.CaBotTestConfiguration;
import ru.v1as.tg.cat.TgBotTest;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.CuriosCatPhaseTests.CuriosConfigurationTest;
import ru.v1as.tg.cat.tg.TgSender;
import ru.v1as.tg.cat.utils.AssertSendMessage;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CaBotTestConfiguration.class, CuriosConfigurationTest.class})
public class CuriosCatPhaseTests extends TgBotTest {

    @Autowired List<AbstractCuriosCatPhase> phases;
    @Autowired TgSender sender;

    @Test
    public void test1() {
        for (AbstractCuriosCatPhase phase : phases) {
            testPhase(phase);
            clearMethodsQueue();
        }
    }

    private void testPhase(AbstractCuriosCatPhase phase) {
        phase.open(getTgChat(), getTgChat(), getTgUser(), getMessage());
        final AssertSendMessage message = popSendMessage();
        System.out.println(message);
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfigurationTest {}
}
