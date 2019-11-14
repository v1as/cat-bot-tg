package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Service
@Transactional
@RequiredArgsConstructor
public class CatEventService {

    private final CatUserEventDao catUserEventDao;
    private final UserDao userDao;
    private final ChatDao chatDao;

    public void saveCuriosCat(TgUser user, TgChat chat, Message voteMessage) {
        final CatUserEvent event = buildCatEvent(chat, user, voteMessage);
        event.setCatType(CURIOS_CAT);
        event.setResult(CAT1);
        catUserEventDao.save(event);
    }

    public void saveCuriosCatQuest(
            TgUser user, TgChat chat, Message voteMessage, CatRequestVote result, String quest) {
        final CatUserEvent event = buildCatEvent(chat, user, voteMessage);
        event.setCatType(CURIOS_CAT);
        event.setResult(result);
        event.setQuestName(quest);
        catUserEventDao.save(event);
    }

    public void poll(TgChat chat, TgUser user, Message voteMessage, CatRequestVote vote) {
        final CatUserEvent event = buildCatEvent(chat, user, voteMessage);
        event.setCatType(REAL);
        event.setResult(vote);
        catUserEventDao.save(event);
    }

    private CatUserEvent buildCatEvent(TgChat chat, TgUser user, Message voteMessage) {
        final UserEntity userEntity = userDao.getOne(user.getId());
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        final CatUserEvent event = new CatUserEvent();
        event.setChat(chatEntity);
        event.setUser(userEntity);
        event.setMessageId(voteMessage.getMessageId());
        return event;
    }
}
