package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;
import static ru.v1as.tg.cat.service.init.ResourceService.MONEY;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.jpa.entities.events.CatVoteEvent;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Service
@Transactional
@RequiredArgsConstructor
public class CatEventService {

    private static final BigDecimal CAT_REWARD = new BigDecimal(3);
    private final UserEventDao userEventDao;
    private final UserDao userDao;
    private final ChatDao chatDao;

    public void saveCuriosCat(TgUser user, TgChat chat, Integer messageId) {
        final CatUserEvent event = new CatUserEvent();
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        final UserEntity owner = userDao.getOne(user.getId());
        event.setChat(chatEntity);
        event.setUser(owner);
        event.setMessageId(messageId);
        event.setCatType(CURIOS_CAT);
        event.setResult(CAT1);
        userEventDao.save(event);
        userEventDao.save(new ResourceEvent(MONEY, CAT_REWARD, event, owner, chatEntity));
    }

    public void saveCuriosCatQuest(
            TgUser user, TgChat chat, Message voteMessage, CatRequestVote result, String quest) {
        final CatUserEvent event = new CatUserEvent();
        final UserEntity owner = userDao.getOne(user.getId());
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        event.setUser(owner);
        event.setMessageId(voteMessage.getMessageId());
        event.setCatType(CURIOS_CAT);
        event.setResult(result);
        event.setQuestName(quest);
        userEventDao.save(event);
        userEventDao.save(new ResourceEvent(MONEY, CAT_REWARD, event, owner, chatEntity));
    }

    public void saveRealCatPoll(CatRequest req) {
        List<UserEvent> events = new ArrayList<>();
        final CatUserEvent event = new CatUserEvent();
        final ChatEntity chat = chatDao.getOne(req.getChatId());
        final UserEntity owner = userDao.getOne(req.getOwner().getId());
        event.setUser(owner);
        event.setMessageId(req.getMessageId());
        event.setChat(chat);
        event.setCatType(REAL);
        event.setResult(req.getResult());
        events.add(event);
        final int cats = req.getResult().getAmount();
        if (cats > 0) {
            for (Entry<Integer, CatRequestVote> vote : req.getVotes().entrySet()) {
                final UserEntity voter = userDao.getOne(vote.getKey());
                final CatVoteEvent voteEvent = new CatVoteEvent();
                voteEvent.setMessageId(req.getMessageId());
                voteEvent.setChat(chat);
                voteEvent.setUser(voter);
                voteEvent.setDate(LocalDateTime.now());
                events.add(voteEvent);
                events.add(new ResourceEvent(MONEY, BigDecimal.ONE, voteEvent, voter, chat));
            }
            final BigDecimal catsRewards = CAT_REWARD.multiply(new BigDecimal(cats));
            events.add(new ResourceEvent(MONEY, catsRewards, event, owner, chat));
        }
        userEventDao.saveAll(events);
    }
}
