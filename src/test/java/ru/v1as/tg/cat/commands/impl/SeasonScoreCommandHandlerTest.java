package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class SeasonScoreCommandHandlerTest extends AbstractScoreCommandHandlerTest {
    @Test
    public void shouldSendEmptyScoreData() {
        sendCommand("/season_score");
        popSendMessage().assertText("Пока что тут пусто");
        processPoll();

        sendCommand("/season_score");
        popSendMessage().assertContainText("@User0: 3");
    }

}
