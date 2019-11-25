package ru.v1as.tg.cat;

import java.util.Collection;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.model.CatRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class AbstractCatBotTest extends TgBotTest {

    public CatBotData getCatBotData() {
        return catBotData;
    }

    public CatRequest getOnlyOneCatRequest() {
        Collection<CatRequest> catRequests =
                getCatBotData().getChatData(getTgChat()).getCatRequests();
        Assert.assertEquals(1, catRequests.size());
        return catRequests.iterator().next();
    }
}
