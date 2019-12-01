package ru.v1as.tg.cat.callbacks.phase.curios_cat;

/*
import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import org.junit.Before;
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

    @Before
    public void before() {
        switchFirstUserChat();
        sendTextMessage("init");
        switchToPublicChat();
        sendTextMessage("init");
    }

    @Test
    public void test1() {
        for (AbstractCuriosCatPhase phase : phases) {
            testPhase(phase);
            clearMethodsQueue();
        }
    }

    private void testPhase(AbstractCuriosCatPhase phase) {
        phase.open(getTgChat(), getTgChat(), getTgUser(), getMessage());
        TestPath path = new TestPath();
        AssertSendMessage message;
        int counter = 0;
        do {
            do {
                message = popSendMessage();
                if (message.hasCallbacks()) {
                    path.addPoll(message);
                } else {
                    path.addMessage(message);
                }
            } while (!message.hasCallbacks() || message.containText("Любопытный Кот убежал"));
            if (message.hasCallbacks()) {
                counter++;
                message.getCallbacks().get(0).send();
            }
        } while (!message.containText("Любопытный Кот убежал") || counter > 100);
        System.out.println(message);
    }

    private static enum TestPathItemType {
        MESSAGE,
        POLL
    }

    @Configuration
    @ComponentScan("ru.v1as.tg.cat.callbacks.phase.curios_cat")
    public static class CuriosConfigurationTest {}

    private static class TestPath {
        boolean finished = false;
        private List<TestPathItem> items = new ArrayList<>();

        public void addPoll(AssertSendMessage message) {
            items.add(new TestPathItem(TestPathItemType.MESSAGE, message));
        }

        public void addMessage(AssertSendMessage message) {
            items.add(new TestPathItem(TestPathItemType.POLL, message));
        }
    }

    @Value
    private static class TestPathItem {
        TestPathItemType type;
        AssertSendMessage message;
    }
}
*/
