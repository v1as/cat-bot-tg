package ru.v1as.tg.cat.commands.impl;

import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.utils.AssertSendMessage;

public abstract class AbstractCatBotTestWithPoll extends AbstractCatBotTest {

    public void processPoll() {
        switchToFirstUser();

        sendCommand("/enable_polls");
        clearMethodsQueue();

        sendPhotoMessage();
        final AssertSendMessage message = popSendMessage().assertContainText("Это кот?");

        switchToSecondUser();
        message.findCallback("x3").send();
        popAnswerCallbackQuery().assertText("Голос учтён");

        switchToThirdUser();
        message.findCallback("x3").send();
        popAnswerCallbackQuery().assertText("Голос учтён");

        switchToFourthUser();
        message.findCallback("x3").send();
        popAnswerCallbackQuery().assertText("Голос учтён");

        popEditMessageText().assertText("3x" + EmojiConst.CAT);

        clearMethodsQueue();

        switchToFirstUser();
    }
}
