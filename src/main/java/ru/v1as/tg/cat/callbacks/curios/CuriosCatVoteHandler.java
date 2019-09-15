package ru.v1as.tg.cat.callbacks.curios;

import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.TgCallBackHandler;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.model.DbData;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@RequiredArgsConstructor
public class CuriosCatVoteHandler implements TgCallBackHandler<String> {

    private final DbData<CatChatData> data;
    private final ScoreData scoreData;
    private final UnsafeAbsSender sender;

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
