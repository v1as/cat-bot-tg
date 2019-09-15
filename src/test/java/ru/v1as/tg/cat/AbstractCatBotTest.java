package ru.v1as.tg.cat;

import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Collection;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;

@SuppressWarnings("unchecked")
public class AbstractCatBotTest extends AbstractGameBotTest {

    {
        bot =
                new CatBot(mock(ScoreData.class)) {
                    @Override
                    public <T extends Serializable, Method extends BotApiMethod<T>> T executeUnsafe(
                            Method method) {
                        methods.add(method);
                        return null;
                    }

                    @Override
                    public <
                                    T extends Serializable,
                                    Method extends BotApiMethod<T>,
                                    Callback extends SentCallback<T>>
                            void executeAsyncUnsafe(Method method, Callback callback) {
                        methods.add(method);
                        Message message = getMessage(++lastMsgId);
                        callback.onResult(method, (T) message);
                    }
                };
    }

    public DbData<CatChatData> getCatBotData() {
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
