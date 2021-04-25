package ru.v1as.tg.cat;

import static org.junit.Assert.assertEquals;
import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;

import java.util.Collection;
import java.util.stream.Stream;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;
import ru.v1as.tg.cat.model.CatRequest;

@Rollback
@Transactional
@RunWith(SpringRunner.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@SpringBootTest(classes = CaBotTestConfiguration.class)
public abstract class AbstractCatBotTest extends TgBotTest {

    @Before
    public void before() {
        super.before();
        catBotData.clear();
        Stream.of(bob, mary).forEach(u -> u.inPrivate().sendTextMessage("init"));
        Stream.of(bob, mary).forEach(u -> u.inPublic().sendTextMessage("init"));
        bob.inPublic(inAnotherPublic).sendTextMessage("init");

        clearMethodsQueue();
    }

    public CatBotData getCatBotData() {
        return catBotData;
    }

    public CatRequest getOnlyOneCatRequest() {
        Collection<CatRequest> catRequests =
                getCatBotData().getChatData(wrap(inPublic.getChat())).getCatRequests();
        assertEquals(1, catRequests.size());
        return catRequests.iterator().next();
    }
}
