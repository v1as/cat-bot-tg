package ru.v1as.tg.cat;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.util.Collection;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.CatRequest;

@Rollback
@Transactional
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = CaBotTestConfiguration.class)
public abstract class AbstractCatBotTest extends TgBotTest {

    @Before
    public void before() {
        sender.setMessageProducer((chatId, text) -> {
            final Message message = getMessage(++lastMsgId, chatId);
            setField(message, "text", text);
            return message;
        });
        lastMsgId = 0;
        lastCallbackQueryId = 0;
        catBotData.clear();

        switchFirstUserChat();
        sendTextMessage("init");

        switchToPublicChat();
        sendTextMessage("init");

        clearMethodsQueue();
    }

    public CatBotData getCatBotData() {
        return catBotData;
    }

    public CatRequest getOnlyOneCatRequest() {
        Collection<CatRequest> catRequests =
                getCatBotData().getChatData(getTgChat()).getCatRequests();
        assertEquals(1, catRequests.size());
        return catRequests.iterator().next();
    }
}
