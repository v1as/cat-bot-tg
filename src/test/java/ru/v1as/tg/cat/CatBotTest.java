package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.tasks.RequestsChecker;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaBotTestConfiguration.class)
public class CatBotTest extends AbstractCatBotTest {

    @Test
    public void testUserPollHimSelfForbidden() {
        sendPhotoMessage();
        popSendMessage("Это кот?");

        sendCallback(lastMsgId, CAT1.getCallback());
        popAnswerCallbackQuery("Вам запрещено голосовать");
    }

    @Test
    public void testUserCancelHimSelf() {
        sendPhotoMessage();
        popSendMessage("Это кот?");

        sendCallback(lastMsgId, NOT_CAT.getCallback());
        popAnswerCallbackQuery("Вы закрыли голосование");
        popDeleteMessage().getMessageId();
    }

    @Test
    public void testUsersPolling() {
        sendPhotoMessage();
        popSendMessage("Это кот?");
        Integer pollMsdId = this.lastMsgId;
        CatRequest catRequest = getOnlyOneCatRequest();

        switchToSecondUser();
        sendCallback(pollMsdId, CAT1.getCallback());
        popAnswerCallbackQuery("Голос учтён");
        popEditMessageReplyMarkup();

        CatRequestVote vote = catRequest.getVotes().entrySet().iterator().next().getValue();
        assertEquals(CAT1, vote);

        switchToThirdUser();
        sendCallback(pollMsdId, CAT1.getCallback());
        popAnswerCallbackQuery("Голос учтён");
        popEditMessageReplyMarkup();
        assertEquals(2, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));

        switchToFourthUser();
        sendCallback(pollMsdId, CAT1.getCallback());
        popAnswerCallbackQuery("Голос учтён");
        popEditMessageReplyMarkup();
        assertEquals(3, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));

        assertFalse(catRequest.isFinished());
        new RequestsChecker(sender, getCatBotData(), catEventService).run();
        assertTrue(catRequest.isFinished());
        popEditMessageText("1x" + EmojiConst.CAT);
    }

    private CatRequest getCatRequest() {
        return new CatRequest(getTgUser(), getMessageUpdate().getMessage().getMessageId(), getTgChat().getId());
    }
}
