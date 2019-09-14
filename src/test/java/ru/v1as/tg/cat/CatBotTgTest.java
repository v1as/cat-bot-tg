package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callback.is_cat.CatRequestVote.NOT_CAT;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
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

    @Test
    public void twoPlayersShouldHaveFirstPlayer() {
        List<String> medals = CatBot.getPlayersWithMedals(props(2, 2));
        medals.forEach(m -> assertTrue(m.startsWith(FIRST_PLACE_MEDAL)));
        assertEquals(2, medals.size());
    }

    @Test
    public void fivePlayersShouldHaveFirstPlayer() {
        List<String> medals = CatBot.getPlayersWithMedals(props(2, 2, 2, 2, 2));
        medals.forEach(m -> assertTrue(m.startsWith(FIRST_PLACE_MEDAL)));
        assertEquals(5, medals.size());
    }

    private LongProperty[] props(long... values) {
        return Arrays.stream(values).mapToObj(this::prop).toArray(LongProperty[]::new);
    }

    private LongProperty prop(long value) {
        return new LongProperty("1", value);
    }

    @Test
    public void threePlayersShouldHaveThreeMedals() {
        List<String> medals = CatBot.getPlayersWithMedals(props(1, 2, 3));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(3, medals.size());
    }

    @Test
    public void sixPeopleWithRepeatsScoreShouldHave3TypeMedals() {
        List<String> medals = CatBot.getPlayersWithMedals(props(6, 6, 5, 5, 1, 1));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(3).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(4).startsWith(THIRD_PLACE_MEDAL));
        assertTrue(medals.get(5).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(6, medals.size());
    }

    @Test
    public void onlyThreePeopleFrom4ShouldHaveMedals() {
        List<String> medals = CatBot.getPlayersWithMedals(props(1, 2, 3, 4));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(3, medals.size());
    }

    private CatRequest getCatRequest() {
        return new CatRequest(
                getMessageUpdate().getMessage(),
                new UserData(getUser()),
                new ChatData(getChat(), false),
                LocalDateTime.now());
    }
}
