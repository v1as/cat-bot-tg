package ru.v1as.tg.cat;

import java.util.Collection;
import org.junit.Assert;
import ru.v1as.tg.cat.model.CatRequest;

public class AbstractCatBotTest extends TgBotTest {

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
