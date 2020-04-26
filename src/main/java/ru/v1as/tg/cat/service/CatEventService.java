package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.REAL;
import static ru.v1as.tg.cat.jpa.entities.user.ChatUserParam.CONCENTRATION_POTION;
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

    public static final int VOTE_REWARD = 1;
    private final ChatParamResource params;
    private final UserEventDao userEventDao;
    private final UserDao userDao;
    private final ChatDao chatDao;

    public CatRequestVote saveCuriosCat(TgUser user, TgChat chat, Integer messageId) {
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        final UserEntity owner = userDao.getOne(user.getId());
        final CatRequestVote result = concentration(chatEntity, owner, CAT1);
        final CatUserEvent event =
                new CatUserEvent(chatEntity, owner, messageId, CURIOS_CAT, result);
        userEventDao.save(event);
        params.increment(chatEntity, owner, MONEY, result.reward());
        return result;
    }

    private CatRequestVote concentration(ChatEntity chat, UserEntity user, CatRequestVote cats) {
        final boolean concentration =
                params.paramBool(chat.getId(), user.getId(), CONCENTRATION_POTION);
        return concentration ? cats.increment() : cats;
    }

    public CatRequestVote saveCuriosCatQuest(
            TgUser user, TgChat tgChat, Message voteMessage, CatRequestVote cats, String quest) {
        final UserEntity owner = userDao.getOne(user.getId());
        final ChatEntity chat = chatDao.getOne(tgChat.getId());
        cats = concentration(chat, owner, cats);
        final CatUserEvent event =
                new CatUserEvent(chat, owner, voteMessage.getMessageId(), CURIOS_CAT, cats);
        event.setQuestName(quest);
        userEventDao.save(event);
        params.increment(chat, owner, MONEY, CatRequestVote.CAT_REWARD * cats.getAmount());
        return cats;
    }

    public void saveRealCatPoll(CatRequest req) {
        List<UserEvent> events = new ArrayList<>();
        final ChatEntity chat = chatDao.getOne(req.getChatId());
        final UserEntity owner = userDao.getOne(req.getOwner().getId());
        final CatRequestVote cats = concentration(chat, owner, req.getResult());
        final CatUserEvent event = new CatUserEvent(chat, owner, req.getMessageId(), REAL, cats);
        events.add(event);
        if (cats.getAmount() > 0) {
            for (Entry<Integer, CatRequestVote> vote : req.getVotes().entrySet()) {
                final UserEntity voter = userDao.getOne(vote.getKey());
                final CatVoteEvent voteEvent =
                        new CatVoteEvent(chat, voter, event, vote.getValue());
                events.add(voteEvent);
                params.increment(chat, voter, MONEY, VOTE_REWARD);
            }
            params.increment(chat, owner, MONEY, CatRequestVote.CAT_REWARD * cats.getAmount());
        }
        userEventDao.saveAll(events);
    }
}
