package ru.v1as.tg.cat.messages;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVoteHandler;
import ru.v1as.tg.cat.callbacks.is_cat.IsCatPollCallback;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CatRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class CatRequestMessageCreator implements MessageHandler {

    private final CatBotData data;
    private final TgSender sender;

    @Override
    public void handle(Message msg, TgChat chat, TgUser user) {
        if (isInvalidIncomeMessage(msg)) {
            return;
        }
        CatChatData chatData = data.getChatData(chat);
        final Integer msgId = msg.getMessageId();
        CatRequest catRequest = new CatRequest(user, msgId, chat.getId());
        catRequest.setIsReal(true);
        log.info("Cat poll registered for user '{}' with message {}", user, msgId);
        sender.executeAsync(
                buildIsThatCatMessage(msg, chat, catRequest),
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
