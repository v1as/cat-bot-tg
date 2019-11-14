package ru.v1as.tg.cat.callbacks.curios;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.SimpleCallbackHandler;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
public class CuriosCatVoteHandler extends SimpleCallbackHandler {

    private static final String CURIOS_CAT_CB = "curiosCat";

    private final CatBotData data;
    private final ScoreData scoreData;
    private final UnsafeAbsSender sender;
    private CatUserEventDao catUserEventDao;
    private UserDao userDao;
    private ChatDao chatDao;

    public CuriosCatVoteHandler(CatBotData data1, ScoreData scoreData, UnsafeAbsSender sender) {
        super(CURIOS_CAT_CB);
        this.data = data1;
        this.scoreData = scoreData;
        this.sender = sender;
    }

    @Override
    public void handle(String value, TgChat chat, TgUser user, CallbackQuery callbackQuery) {
        CatChatData chatData = data.getChatData(chat.getId());
        CuriosCatRequest request =
                chatData.getCuriosCatRequest(callbackQuery.getMessage().getMessageId());
        if (request == null) {
            sender.executeUnsafe(deleteMsg(callbackQuery.getMessage()));
            return;
        }
        request.finish(user.toString());
        sender.executeUnsafe(deleteMsg(request.getVoteMessage()));
        sender.executeUnsafe(
                new SendMessage()
                        .setChatId(chat.getId())
                        .setText(
                                "Любопытный Кот убежал к "
                                        + user.getUsernameOrFullName()
                                        + "   "
                                        + EmojiConst.CAT));
        CatRequest catRequest = new CatRequest(request.getVoteMessage(), user, chat);
        catRequest.finish(CAT1);
        scoreData.save(catRequest);
        final UserEntity userEntity = userDao.getOne(user.getId());
        final ChatEntity chatEntity = chatDao.getOne(chat.getId());
        catUserEventDao.save(
                CatUserEvent.curiosCat(chatEntity, userEntity, request.getVoteMessage()));
    }
}
