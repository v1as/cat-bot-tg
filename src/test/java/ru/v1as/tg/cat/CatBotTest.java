package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.springframework.transaction.annotation.Transactional;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;

@Slf4j
public class CatBotTest extends AbstractCatBotTest {

    @Before
    @SneakyThrows
    public void init() {
        sendCommand("/enable_polls");
        clearMethodsQueue();
    }

    @Test
    @Transactional
    public void testUserPollHimSelfForbidden() {
        sendPhotoMessage();
        popSendMessage().assertText("Это кот?");

        sendCallback(lastMsgId, CAT1.getCallback());
        popAnswerCallbackQuery("Вам запрещено голосовать");
    }

    @Test
    public void testUserCancelHimSelf() {
        sendPhotoMessage();
        popSendMessage()
            .assertText("Это кот?")
            .findCallback(EmojiConst.HEAVY_MULTIPLY)
            .send();
        popAnswerCallbackQuery("Вы закрыли голосование");
        popDeleteMessage().getMessageId();
    }

    @Test
    public void testUsersPolling() {
        sendPhotoMessage();
        popSendMessage().assertText("Это кот?");
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
        assertEquals(3, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));
        assertTrue(catRequest.isClosed());
        popEditMessageText("1x" + EmojiConst.CAT);
    }
}
