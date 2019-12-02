package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.utils.AssertCallback;
import ru.v1as.tg.cat.utils.AssertSendMessage;

@Slf4j
public class CatBotTest extends AbstractCatBotTest {

    @Before
    @SneakyThrows
    public void init() {
        sendCommand("/enable_polls");
        clearMethodsQueue();
    }

    @Test
    public void testUserPollHimSelfForbidden() {
        sendPhotoMessage();
        popSendMessage().assertText("Это кот?");

        sendCallback(lastMsgId, CAT1.getCallback());
        popAnswerCallbackQuery().assertText("Вам запрещено голосовать");
    }

    @Test
    public void testUserCancelHimSelf() {
        sendPhotoMessage();
        popSendMessage().assertText("Это кот?").findCallback(EmojiConst.HEAVY_MULTIPLY).send();
        popAnswerCallbackQuery().assertText("Вы закрыли голосование");
        popDeleteMessage().getMessageId();
    }

    @Test
    public void testUsersPolling() {
        sendPhotoMessage();
        AssertSendMessage message = popSendMessage().assertText("Это кот?");
        Integer pollMsdId = this.lastMsgId;
        CatRequest catRequest = getOnlyOneCatRequest();

        switchToSecondUser();
        message.getCallbacks().get(0).send();
        popAnswerCallbackQuery().assertContainText("Голос учтён");
        popEditMessageReplyMarkup();

        CatRequestVote vote = catRequest.getVotes().entrySet().iterator().next().getValue();
        assertEquals(CAT1, vote);

        switchToThirdUser();
        sendCallback(pollMsdId, CAT1.getCallback());
        popAnswerCallbackQuery().assertContainText("Голос учтён");
        popEditMessageReplyMarkup();
        assertEquals(2, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));

        switchToFourthUser();
        sendCallback(pollMsdId, CAT1.getCallback());
        popAnswerCallbackQuery().assertContainText("Голос учтён");
        assertEquals(3, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));
        assertTrue(catRequest.isClosed());
        popEditMessageText().assertText("1x" + CAT);
    }

    @Test
    public void testUserChangeVote() {
        sendPhotoMessage();
        final AssertSendMessage message = popSendMessage();
        switchToSecondUser();
        message.assertText("Это кот?").getCallbacks().get(0).assertText(CAT).send();
        popAnswerCallbackQuery().assertContainText("Голос учтён");
        List<AssertCallback> callbacks = popEditMessageReplyMarkup().getCallbacks();
        callbacks.get(0).assertText("(1)" + CAT);
        callbacks.get(1).assertText(CAT + "x2");
        callbacks.get(2).assertText(CAT + "x3");

        message.assertText("Это кот?").getCallbacks().get(1).assertText(CAT + "x2").send();
        popAnswerCallbackQuery().assertContainText("Голос изменён");
        callbacks = popEditMessageReplyMarkup().getCallbacks();
        callbacks.get(0).assertText(CAT);
        callbacks.get(1).assertText("(1)" + CAT + "x2");
        callbacks.get(2).assertText(CAT + "x3");
    }
}
