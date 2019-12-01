package ru.v1as.tg.cat.commands.impl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;

public class DisablePollsCommandTest extends AbstractCatBotTest {

    @Autowired ChatDetailsDao chatDetailsDao;

    @Test
    public void shouldEnableAndDisablePollsInChat() {
        assertFalse(chatDetailsDao.getOne(getChatId()).isCatPollEnabled());

        sendCommand("/enable_polls");
        popSendMessage().assertText("Создание опросов теперь включено");
        assertTrue(chatDetailsDao.getOne(getChatId()).isCatPollEnabled());

        sendCommand("/enable_polls");
        popSendMessage().assertText("Создание опросов уже включено");
        assertTrue(chatDetailsDao.getOne(getChatId()).isCatPollEnabled());

        sendCommand("/disable_polls");
        popSendMessage().assertText("Создание опросов теперь выключено");
        assertFalse(chatDetailsDao.getOne(getChatId()).isCatPollEnabled());

        sendCommand("/disable_polls");
        popSendMessage().assertText("Создание опросов уже выключено");
        assertFalse(chatDetailsDao.getOne(getChatId()).isCatPollEnabled());
    }
}
