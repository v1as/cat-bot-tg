package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.v1as.tg.cat.EmojiConst.CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;

import java.util.List;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.tg.TestUserChat;
import ru.v1as.tg.cat.utils.AssertCallback;
import ru.v1as.tg.cat.utils.AssertSendMessageToSend;

@Slf4j
public class CatBotTest extends AbstractCatBotTest {

    @Before
    @SneakyThrows
    public void init() {
        bob.inPublic().sendCommand("/enable_polls");
        clearMethodsQueue();
    }

    @Test
    public void testUserPollHimSelfForbidden() {
        final TestUserChat chat = bob.inPublic();

        chat.sendPhotoMessage();
        chat.getSendMessageToSend().assertText("Это кот?").findCallbackToSend("x3").send();
        chat.getAnswerCallbackQuery().assertText("Вам запрещено голосовать");
    }

    @Test
    public void testUserCancelHimSelf() {
        final TestUserChat chat = bob.inPublic();
        chat.sendPhotoMessage();
        chat.getSendMessageToSend()
                .assertText("Это кот?")
                .findCallbackToSend(EmojiConst.HEAVY_MULTIPLY)
                .send();
        chat.getAnswerCallbackQuery().assertText("Вы закрыли голосование");
        chat.getDeleteMessage().assertTextContains("Это кот?");
    }

    @Test
    public void testUsersPolling() {
        bob.inPublic().sendPhotoMessage();
        CatRequest catRequest = getOnlyOneCatRequest();

        mary.inPublic().findSendMessageToSend("Это кот?").firstCallbacksToSend().send();
        mary.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");
        public0.getEditMessageReplyMarkup();

        CatRequestVote vote = catRequest.getVotes().entrySet().iterator().next().getValue();
        assertEquals(CAT1, vote);

        jho.inPublic().findSendMessageToSend("Это кот?").firstCallbacksToSend().send();
        jho.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");
        public0.getEditMessageReplyMarkup();

        assertEquals(2, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));

        zakh.inPublic()
                .getSendMessageToSend()
                .assertText("Это кот?")
                .getCallbacksToSend()
                .get(0)
                .send();
        zakh.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");

        public0.getEditMessage().assertText("1x" + CAT);

        assertEquals(3, catRequest.getVotes().size());
        assertTrue(catRequest.getVotes().values().stream().allMatch(CAT1::equals));
        assertTrue(catRequest.isClosed());
    }

    @Test
    public void testUserChangeVote() {
        bob.inPublic().sendPhotoMessage();

        final AssertSendMessageToSend msg =
                mary.inPublic().getSendMessageToSend().assertText("Это кот?");
        msg.firstCallbacksToSend().send();
        mary.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");

        List<AssertCallback> callbacks = mary.inPublic().getEditMessageReplyMarkup().getCallbacks();
        callbacks.get(0).assertText("(1)" + CAT);
        callbacks.get(1).assertText(CAT + "x2");
        callbacks.get(2).assertText(CAT + "x3");

        msg.assertText("Это кот?").getCallbacksToSend().get(1).assertText(CAT + "x2").send();
        mary.inPublic().getAnswerCallbackQuery().assertContainText("Голос изменён");

        callbacks = mary.inPublic().getEditMessageReplyMarkup().getCallbacks();
        callbacks.get(0).assertText(CAT);
        callbacks.get(1).assertText("(1)" + CAT + "x2");
        callbacks.get(2).assertText(CAT + "x3");
    }
}
