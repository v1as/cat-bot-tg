package ru.v1as.tg.cat.commands.impl;

import lombok.SneakyThrows;
import org.junit.Before;
import org.junit.Test;
import ru.v1as.tg.cat.service.ChatParam;

public class CheckPollsCommandTest extends AbstractCatBotTestWithPoll {

    @Before
    @SneakyThrows
    public void init() {
        chatParamResource.param(
                inPublic.getId(), bob.getUserId(), ChatParam.PICTURE_POLL_ENABLED, true);
    }

    @Test
    public void no_open_request() {
        mary.inPublic().sendCommand("/check_poll");

        inPublic.getSendMessage().assertContainText("Все опросы закрыты");
    }

    @Test
    public void single_open_request() {
        mary.inPublic().sendPhotoMessage();

        Integer pollMessageId =
                mary.inPublic().getSendMessageToSend().assertText("Это кот?").getMessageId();

        mary.inPublic().sendCommand("/check_poll");

        inPublic.getSendMessage()
                .assertText("Самый ранний не закрытый опрос.\n\n Всего не закрыто: 1")
                .assertReplyTo(pollMessageId);
    }

    @Test
    public void couple_open_requests() {

        zakh.inPublic().sendPhotoMessage();

        Integer pollMessageId = inPublic.getSendMessage().assertText("Это кот?").getMessageId();

        mary.inPublic().sendPhotoMessage();

        mary.inPublic().getSendMessageToSend().assertText("Это кот?").getMessageId();

        mary.inPublic().sendCommand("/check_poll");

        inPublic.getSendMessage()
                .assertText("Самый ранний не закрытый опрос.\n\n Всего не закрыто: 2")
                .assertReplyTo(pollMessageId);
    }
}
