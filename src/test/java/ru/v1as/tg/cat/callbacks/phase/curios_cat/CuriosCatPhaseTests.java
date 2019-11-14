package ru.v1as.tg.cat.callbacks.phase.curios_cat;

import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.AbstractTgBotTest;
import ru.v1as.tg.cat.CaBotTestConfiguration;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.CuriosCatPhaseTests.CuriosConfigurationTest;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {CaBotTestConfiguration.class, CuriosConfigurationTest.class})
public class CuriosCatPhaseTests extends AbstractTgBotTest {

    @Autowired List<AbstractCuriosCatPhase> phases;
    @Autowired UnsafeAbsSender sender;

    @Test
    public void test1() {
        for (AbstractCuriosCatPhase phase : phases) {
            testPhase(phase);
            clearMethodsQueue();
        }
    }

    private void testPhase(AbstractCuriosCatPhase phase) {
        phase.open(getTgChat(), getTgChat(), getTgUser(), getMessage());
        SendMessage message = popSendMessage();
        System.out.println(message);
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfigurationTest {}
}
