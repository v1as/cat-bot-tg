package ru.v1as.tg.cat.messages;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVoteHandler;
import ru.v1as.tg.cat.callbacks.is_cat.IsCatPollCallback;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Component
@RequiredArgsConstructor
public class CatRequestMessageCreator implements MessageHandler {

    private final CatBotData data;
    private final UnsafeAbsSender sender;

    @Override
    public void handle(Message message, TgChat chat, TgUser user) {
        if (isInvalidIncomeMessage(message)) {
            return;
        }
        CatChatData chatData = data.getChatData(chat.getId());
        CatRequest catRequest = new CatRequest(message, user, chat);
        catRequest.setIsReal(true);
        sender.executeAsyncUnsafe(
                buildIsThatCatMessage(message, chat, catRequest),
                new IsCatPollCallback(chatData, catRequest));
    }

    private boolean isInvalidIncomeMessage(Message message) {
        return !message.hasPhoto()
                && !message.hasVideo()
                && !message.hasVideoNote()
                && !message.hasDocument();
    }

    private SendMessage buildIsThatCatMessage(Message message, TgChat chat, CatRequest catRequest) {
        InlineKeyboardMarkup buttons = CatRequestVoteHandler.getCatePollButtons(catRequest);
        catRequest.setPollButtons(buttons);
        return new SendMessage()
                .setReplyToMessageId(message.getMessageId())
                .setChatId(chat.getId())
                .setText("Это кот?")
                .setReplyMarkup(buttons);
    }
}
