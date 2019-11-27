package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class MonthScoreCommandHandlerTest extends AbstractCatBotTestWithPoll {

    @Test
    public void shouldSendEmptyScoreData() {
        sendCommand("/score");
        popSendMessage().assertText("Пока что тут пусто");
        processPoll();

        sendCommand("/score");
        popSendMessage().assertContainText("@User0: 3");
    }
}
