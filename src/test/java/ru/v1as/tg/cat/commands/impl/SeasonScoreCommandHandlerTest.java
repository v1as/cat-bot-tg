package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class SeasonScoreCommandHandlerTest extends AbstractCatBotTestWithPoll {
    @Test
    public void shouldSendEmptyScoreData() {
        bob.inPublic().sendCommand("/season_score");
        public0.getSendMessage().assertText("Пока что тут пусто");
        processPoll();

        bob.inPublic().sendCommand("/season_score");
        public0.getSendMessage().assertContainText("@bob: 3");
    }
}
