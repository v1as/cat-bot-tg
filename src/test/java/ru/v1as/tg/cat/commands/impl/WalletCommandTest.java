package ru.v1as.tg.cat.commands.impl;

import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;

import org.junit.Test;

public class WalletCommandTest extends AbstractCatBotTestWithPoll {

    @Test
    public void shouldReturnMoney() {
        processPoll();
        bob.inPublic().sendCommand("/wallet");
        inPublic.getSendMessage().assertContainText(MONEY_BAG).assertContainText("9");

        mary.inPublic().sendCommand("/wallet");
        inPublic.getSendMessage().assertContainText(MONEY_BAG).assertContainText("1");
    }
}
