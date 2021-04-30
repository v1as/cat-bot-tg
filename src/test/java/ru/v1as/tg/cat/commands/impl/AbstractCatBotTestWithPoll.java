package ru.v1as.tg.cat.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.service.ChatParam;
import ru.v1as.tg.cat.service.ChatParamResource;

public abstract class AbstractCatBotTestWithPoll extends AbstractCatBotTest {

    @Autowired ChatParamResource chatParamResource;

    public void processPoll() {
        chatParamResource.param(inPublic.getId(), bob.getUserId(), ChatParam.PICTURE_POLL_ENABLED, true);
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
