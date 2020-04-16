package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.MONEY;

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
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Service
@Transactional
@RequiredArgsConstructor
public class CatEventService {

    public static final int CAT_REWARD = 3;
    public static final int VOTE_REWARD = 1;
    private final ChatParamResource paramResource;
    private final UserEventDao userEventDao;
    private final UserDao userDao;
    private final ChatDao chatDao;

    public void saveCuriosCat(TgUser user, TgChat chat, Integer messageId) {
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        final UserEntity owner = userDao.getOne(user.getId());
        final CatUserEvent event = new CatUserEvent(chatEntity, owner, messageId, CURIOS_CAT, CAT1);
        userEventDao.save(event);
        paramResource.increment(chatEntity, owner, MONEY, CAT_REWARD);
    }

    public void saveCuriosCatQuest(
            TgUser user, TgChat chat, Message voteMessage, CatRequestVote result, String quest) {
        final UserEntity owner = userDao.getOne(user.getId());
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        final CatUserEvent event =
                new CatUserEvent(chatEntity, owner, voteMessage.getMessageId(), CURIOS_CAT, result);
        event.setQuestName(quest);
        userEventDao.save(event);
        int cats = result.getAmount();
        paramResource.increment(chatEntity, owner, MONEY, CAT_REWARD * cats);
    }

    public void saveRealCatPoll(CatRequest req) {
        List<UserEvent> events = new ArrayList<>();
        final ChatEntity chat = chatDao.getOne(req.getChatId());
        final UserEntity owner = userDao.getOne(req.getOwner().getId());
        final CatUserEvent event =
                new CatUserEvent(chat, owner, req.getMessageId(), REAL, req.getResult());
        events.add(event);
        final int cats = req.getResult().getAmount();
        if (cats > 0) {
            for (Entry<Integer, CatRequestVote> vote : req.getVotes().entrySet()) {
                final UserEntity voter = userDao.getOne(vote.getKey());
                final CatVoteEvent voteEvent =
                        new CatVoteEvent(chat, voter, event, vote.getValue());
                events.add(voteEvent);
                paramResource.increment(chat, voter, MONEY, VOTE_REWARD);
            }
            paramResource.increment(chat, owner, MONEY, CAT_REWARD * cats);
        }
        userEventDao.saveAll(events);
    }
}
