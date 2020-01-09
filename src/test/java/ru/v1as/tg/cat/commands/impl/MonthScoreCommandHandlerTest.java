package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class MonthScoreCommandHandlerTest extends AbstractCatBotTestWithPoll {

    @Test
    public void shouldSendEmptyScoreData() {
        bob.inPublic().sendCommand("/score");
        public0.getSendMessage().assertText("Пока что тут пусто");
        processPoll();

        bob.inPublic().sendCommand("/score");
        public0.getSendMessage().assertContainText("@User1: 3");
    }
}
