package ru.v1as.tg.cat.commands.impl;

import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;

import org.junit.Test;

public class WalletCommandTest extends AbstractCatBotTestWithPoll {

    @Test
    public void shouldReturnMoney() {
        processPoll();
        sendCommand("/wallet");
        popSendMessage().assertContainText(MONEY_BAG).assertContainText("9");

        switchToSecondUser();
        sendCommand("/wallet");
        popSendMessage().assertContainText(MONEY_BAG).assertContainText("1");
    }
}
