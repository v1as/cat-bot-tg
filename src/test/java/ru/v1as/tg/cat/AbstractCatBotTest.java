package ru.v1as.tg.cat;

import java.util.Collection;
import org.junit.Assert;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.ScoreData;

public class AbstractCatBotTest extends AbstractTgBotTest {

    public CatBotData getCatBotData() {
        return getCatBot().getData();
    }

    public CatBot getCatBot() {
        return (CatBot) bot;
    }

    public ScoreData getCatBotScoreData() {
        return (getCatBot()).getData().getScoreData();
    }

    public CatRequest getOnlyOneCatRequest() {
        Collection<CatRequest> catRequests =
                getCatBotData().getChatData(getChatId()).getCatRequests();
        Assert.assertEquals(1, catRequests.size());
        return catRequests.iterator().next();
    }
}
