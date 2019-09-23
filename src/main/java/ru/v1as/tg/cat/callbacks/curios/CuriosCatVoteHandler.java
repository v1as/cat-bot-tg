package ru.v1as.tg.cat.callbacks.curios;

import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.SimpleCallbackHandler;
import ru.v1as.tg.cat.callbacks.TgCallBackHandler;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
public class CuriosCatVoteHandler extends SimpleCallbackHandler
        implements TgCallBackHandler<String> {

    private static final String CURIOS_CAT_CB = "curiosCat";

    private final CatBotData data;
    private final ScoreData scoreData;
    private final UnsafeAbsSender sender;

    public CuriosCatVoteHandler(CatBotData data1, ScoreData scoreData, UnsafeAbsSender sender) {
        super(CURIOS_CAT_CB);
        this.data = data1;
        this.scoreData = scoreData;
        this.sender = sender;
    }

    @Override
    public void handle(String value, Chat chat, User user, CallbackQuery callbackQuery) {
        CatChatData chatData = data.getChatData(chat.getId());
        UserData userData = data.getUserData(user);
        CuriosCatRequest request =
                chatData.getCuriosCatRequest(callbackQuery.getMessage().getMessageId());
        if (request == null) {
            sender.executeUnsafe(deleteMsg(chat.getId(), callbackQuery.getMessage()));
            return;
        }
        request.finish(user.toString());
        sender.executeUnsafe(deleteMsg(chat.getId(), request.getVoteMessage()));
        sender.executeUnsafe(
                new SendMessage()
                        .setChatId(chat.getId())
                        .setText(
                                "Любопытный Кот убежал к "
                                        + userData.getUsernameOrFullName()
                                        + "   "
                                        + EmojiConst.CAT));
        CatRequest catRequest = new CatRequest(request.getVoteMessage(), userData, chatData);
        catRequest.finish(CatRequestVote.CAT1);
        scoreData.save(catRequest);
    }
}
