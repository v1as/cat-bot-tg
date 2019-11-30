package ru.v1as.tg.cat;

import java.util.Collection;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
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
