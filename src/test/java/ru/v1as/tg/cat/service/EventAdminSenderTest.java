package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import ru.v1as.tg.cat.AbstractCatBotTest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@RunWith(SpringRunner.class)
@ActiveProfiles(profiles = "test")
public class EventAdminSenderTest extends AbstractCatBotTest {

    @Autowired private EventAdminSender eventAdminSender;
    @Autowired private UserEventDao userEventDao;
    @Autowired private UserDao userDao;
    @Autowired private ChatDao chatDao;

    @Test
    public void shouldSendNothing() {
        eventAdminSender.sendEvents();
        assertMethodsQueueIsEmpty();
    }

    @Test
    public void shouldSendMessage() {
        UserEntity user = userDao.getOne(bob.getUserId());
        ChatEntity chat = chatDao.getOne(public0.getId());
        userEventDao.save(new CatUserEvent(chat, user, public0.getMessageId(), REAL, CAT1));

        eventAdminSender.sendEvents();
        bob.inPrivate().getSendMessage().assertContainText("CatUserEvent");
    }
}
