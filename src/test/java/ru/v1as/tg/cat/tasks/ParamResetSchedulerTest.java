package ru.v1as.tg.cat.tasks;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.service.ChatParamResource;

public class ParamResetSchedulerTest extends AbstractCatBotTest {

    @Autowired ChatParamResource paramResource;
    @Autowired ChatDao chatDao;
    @Autowired UserDao userDao;
    @Autowired ParamResetScheduler paramResetScheduler;

    @Test
    public void should_reset_params() {
        final ChatEntity chat = chatDao.findById(inPublic.getId()).get();
        final UserEntity user = userDao.findById(bob.getUserId()).get();

        paramResource.param(inPublic.getId(), bob.getUserId(), CONCENTRATION_POTION, "true");
        paramResource.increment(chat, user, CAT_BITE_LEVEL, 5);

        paramResetScheduler.run();

        assertFalse(paramResource.paramBool(inPublic.getId(), bob.getUserId(), CONCENTRATION_POTION));
        assertEquals("0", paramResource.param(chat.getId(), CAT_BITE_LEVEL));
    }
}
