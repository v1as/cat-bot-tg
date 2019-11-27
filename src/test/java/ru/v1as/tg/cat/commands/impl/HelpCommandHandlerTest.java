package ru.v1as.tg.cat.commands.impl;

import org.junit.Test;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class HelpCommandHandlerTest extends AbstractCatBotTest {

    @Test
    public void shouldSendHelpMessage() {
        sendCommand("/help");
        popSendMessage().assertContainText("/score - Вывести счёт за текущий месяц");
    }
}
