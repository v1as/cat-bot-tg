package ru.v1as.tg.cat.callbacks.curios;

import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.SimpleCallbackHandler;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.CatEventService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
public class CuriosCatVoteHandler extends SimpleCallbackHandler {

    private static final String CURIOS_CAT_CB = "curiosCat";

    private final CatBotData data;
    private final TgSender sender;
    private final CatEventService catEventService;

    public CuriosCatVoteHandler(CatBotData data, TgSender sender, CatEventService catEventService) {
        super(CURIOS_CAT_CB);
        this.data = data;
        this.sender = sender;
        this.catEventService = catEventService;
    }

    @Override
    public void handle(String value, TgChat chat, TgUser user, CallbackQuery callbackQuery) {
        CatChatData chatData = data.getChatData(chat);
        CuriosCatRequest request =
                chatData.getCuriosCatRequest(callbackQuery.getMessage().getMessageId());
        if (request == null) {
            sender.execute(deleteMsg(callbackQuery.getMessage()));
            return;
        }
        request.finish(user.getUsernameOrFullName());
        log.info("Request for user '{}' is finished.", user);
        final Integer reqMsgId = request.getMessageId();
        sender.execute(new DeleteMessage(chat.getId(), reqMsgId));
        sender.execute(
                new SendMessage()
                        .setChatId(chat.getId())
                        .setText(
                                "Любопытный Кот убежал к "
                                        + user.getUsernameOrFullName()
                                        + "   "
                                        + EmojiConst.CAT));
        catEventService.saveCuriosCat(user, chat, reqMsgId);
    }
}
