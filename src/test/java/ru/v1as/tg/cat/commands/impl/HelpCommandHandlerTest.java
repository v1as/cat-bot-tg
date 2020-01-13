package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class HelpCommandHandlerTest extends AbstractCatBotTest {

    @Test
    public void shouldSendHelpMessage() {
        bob.inPublic().sendCommand("/help");
        inPublic.getSendMessage().assertContainText("/score - Вывести счёт за текущий месяц");
    }
}
