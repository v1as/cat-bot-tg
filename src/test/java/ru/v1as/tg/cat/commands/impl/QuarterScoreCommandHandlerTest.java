package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class QuarterScoreCommandHandlerTest extends AbstractScoreCommandHandlerTest {

    @Test
    public void shouldSendEmptyScoreData() {
        sendCommand("/quarter_score");
        popSendMessage().assertText("Пока что тут пусто");
        processPoll();

        sendCommand("/quarter_score");
        popSendMessage().assertContainText("@User0: 3");
    }
}
