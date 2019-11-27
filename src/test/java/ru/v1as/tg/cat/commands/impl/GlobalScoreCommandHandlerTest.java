package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class GlobalScoreCommandHandlerTest extends AbstractScoreCommandHandlerTest {

    @Test
    public void shouldSendEmptyScoreData() {
        sendCommand("/global_score");
        popSendMessage().assertText("Пока что тут пусто");
        processPoll();

        sendCommand("/global_score");
        popSendMessage().assertContainText("@User0: 3");
    }

}
