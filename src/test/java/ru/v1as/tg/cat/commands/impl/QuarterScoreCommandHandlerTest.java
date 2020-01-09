package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class QuarterScoreCommandHandlerTest extends AbstractCatBotTestWithPoll {

    @Test
    public void shouldSendEmptyScoreData() {
        bob.inPublic().sendCommand("/quarter_score");
        public0.getSendMessage().assertText("Пока что тут пусто");
        processPoll();

        bob.inPublic().sendCommand("/quarter_score");
        public0.getSendMessage().assertContainText("@bob: 3");
    }
}
