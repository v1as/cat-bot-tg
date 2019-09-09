package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.NOT_CAT;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.Test;
import ru.v1as.tg.cat.ScoreData.ScoreLine;
import ru.v1as.tg.cat.callback.is_cat.CatRequestVote;

public class CatBotTgTest extends AbstractCatBotTest {

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
        getCatBot().checkCatRequests();
        assertTrue(catRequest.isFinished());
        popEditMessageText("1x" + EmojiConst.CAT);
    }

    @Test
    public void scoreTest() {
        String tempFile = "tempFile";
        ScoreData scoreData = new ScoreData(tempFile);
        new File(tempFile).delete();
        scoreData.init();

        CatRequest catRequest = getCatRequest();
        catRequest.finish(CAT1);
        scoreData.save(catRequest);

        CatRequest catRequest2 = getCatRequest();
        catRequest2.finish(CAT1);
        scoreData.save(catRequest2);

        scoreData.flush();
        scoreData.init();
        List<ScoreLine> scoreLines = scoreData.getScore(getChatId());
        assertEquals(2, scoreLines.size());
    }

    private CatRequest getCatRequest() {
        return new CatRequest(
                getMessageUpdate().getMessage(),
                new UserData(getUser()),
                new ChatData(getChat(), false),
                LocalDateTime.now());
    }
}
