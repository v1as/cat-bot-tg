package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;

public class RulesCommandHandlerTest extends AbstractCatBotTestWithPoll {

    @Test
    public void should_send_rules() {
        mary.inPublic().sendCommand("/rules");

        inPublic.getSendMessage().assertContainText("Правила игры");
    }
}
