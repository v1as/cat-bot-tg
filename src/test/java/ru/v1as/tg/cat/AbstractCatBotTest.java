package ru.v1as.tg.cat;

import static org.mockito.Mockito.mock;

import java.io.Serializable;
import java.util.Map.Entry;
import java.util.Set;
import org.junit.Assert;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.updateshandlers.SentCallback;

@SuppressWarnings("unchecked")
class AbstractCatBotTest extends AbstractGameBotTest {

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

    DbData getCatBotData() {
        return getCatBot().getData();
    }

    CatBot getCatBot() {
        return (CatBot) bot;
    }

    ScoreData getCatBotScoreData() {
        return (getCatBot()).getScoreData();
    }

    CatRequest getOnlyOneCatRequest() {
        Set<Entry<Integer, CatRequest>> entries =
                getCatBotData().getChatData(getChatId()).getCatRequests().entrySet();
        Assert.assertEquals(1, entries.size());
        return entries.iterator().next().getValue();
    }
}
