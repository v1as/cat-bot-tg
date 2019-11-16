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

    public void saveCuriosCat(TgUser user, TgChat chat, Integer messageId) {
        final CatUserEvent event = buildCatEvent(messageId, chat.getId(), user.getId());
        event.setCatType(CURIOS_CAT);
        event.setResult(CAT1);
        catUserEventDao.save(event);
    }

    public void saveCuriosCatQuest(
            TgUser user, TgChat chat, Message voteMessage, CatRequestVote result, String quest) {
        final CatUserEvent event =
                buildCatEvent(voteMessage.getMessageId(), chat.getId(), user.getId());
        event.setCatType(CURIOS_CAT);
        event.setResult(result);
        event.setQuestName(quest);
        catUserEventDao.save(event);
    }

    public void poll(CatRequestVote vote, Integer messageId, Long chatId, Integer userId) {
        final CatUserEvent event = buildCatEvent(messageId, chatId, userId);
        event.setCatType(REAL);
        event.setResult(vote);
        catUserEventDao.save(event);
    }

    private CatUserEvent buildCatEvent(Integer messageId, Long chatId, Integer userId) {
        final UserEntity userEntity = userDao.getOne(userId);
        final ChatEntity chatEntity = chatDao.getOne(chatId);
        final CatUserEvent event = new CatUserEvent();
        event.setChat(chatEntity);
        event.setUser(userEntity);
        event.setMessageId(messageId);
        return event;
    }
}
