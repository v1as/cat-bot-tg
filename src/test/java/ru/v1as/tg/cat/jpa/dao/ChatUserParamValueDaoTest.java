package ru.v1as.tg.cat.jpa.dao;

import static org.junit.Assert.assertFalse;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;

public class ChatUserParamValueDaoTest extends AbstractCatBotTest {
    @Autowired private ChatUserParamValueDao dao;

    @Test
    public void name() {
        assertFalse(dao.findByChatIdAndUserIdAndParam(1L, 1, MONEY).isPresent());
    }
}
