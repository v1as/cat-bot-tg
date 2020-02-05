package ru.v1as.tg.cat.commands.impl;

import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.EmojiConst;

public abstract class AbstractCatBotTestWithPoll extends AbstractCatBotTest {

    public void processPoll() {
        bob.inPublic().sendCommand("/enable_polls");
        clearMethodsQueue();

        bob.inPublic().sendPhotoMessage();

        mary.inPublic().findSendMessageToSend("Это кот?").findCallbackToSend("x3").send();
        mary.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");

        jho.inPublic().findSendMessageToSend("Это кот?").findCallbackToSend("x3").send();
        jho.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");

        zakh.inPublic().findSendMessageToSend("Это кот?").findCallbackToSend("x3").send();
        zakh.inPublic().getAnswerCallbackQuery().assertContainText("Голос учтён");

        inPublic.getEditMessage().assertText("3x" + EmojiConst.CAT);

        clearMethodsQueue();
    }
}
