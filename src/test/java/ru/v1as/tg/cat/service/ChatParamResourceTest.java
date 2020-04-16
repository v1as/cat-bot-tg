package ru.v1as.tg.cat.service;

import static org.junit.Assert.assertEquals;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;
import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
public class ChatParamResourceTest extends AbstractCatBotTest {

    @Autowired private ChatParamResource chatParamResource;
    @Autowired private ChatDao chatDao;
    @Autowired private UserDao useDao;

    @Test
    public void save_chat_user_param() {
        final ChatEntity chat = chatDao.getOne(inPublic.getId());
        final UserEntity bob = useDao.getOne(this.bob.getUserId());
        assertEquals("0", chatParamResource.param(chat, bob, MONEY));

        chatParamResource.increment(chat, bob, MONEY, 1);
        assertEquals("1", chatParamResource.param(chat, bob, MONEY));

        chatParamResource.increment(chat, bob, MONEY, 3);
        assertEquals("4", chatParamResource.param(chat, bob, MONEY));

        chatParamResource.increment(chat, bob, MONEY, -4);
        assertEquals("0", chatParamResource.param(chat, bob, MONEY));
    }

    @Test
    public void save_chat_param() {
        final ChatEntity chat = chatDao.getOne(inPublic.getId());
        final UserEntity bob = useDao.getOne(this.bob.getUserId());
        final Long chatId = chat.getId();

        assertEquals("0", chatParamResource.param(chatId, CAT_BITE_LEVEL));

        chatParamResource.increment(chat, bob, CAT_BITE_LEVEL, 1);
        assertEquals("1", chatParamResource.param(chatId, CAT_BITE_LEVEL));

        chatParamResource.increment(chat, bob, CAT_BITE_LEVEL, 4);
        assertEquals("5", chatParamResource.param(chatId, CAT_BITE_LEVEL));

        chatParamResource.increment(chat, bob, CAT_BITE_LEVEL, 1);
        assertEquals("5", chatParamResource.param(chatId, CAT_BITE_LEVEL));

        chatParamResource.increment(chat, bob, CAT_BITE_LEVEL, -5);
        assertEquals("0", chatParamResource.param(chatId, CAT_BITE_LEVEL));
    }
}
