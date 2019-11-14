package ru.v1as.tg.cat.callbacks;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Component
@RequiredArgsConstructor
public class ClearButtonsCallbackHandler implements DefaultTgCallbackHandler {

    private final UnsafeAbsSender sender;

    @Override
    public void handle(TgChat chat, TgUser user, CallbackQuery callbackQuery) {
        Message msg = callbackQuery.getMessage();
        sender.executeUnsafe(
                new EditMessageReplyMarkup()
                        .setMessageId(msg.getMessageId())
                        .setChatId(msg.getChatId()));
    }
}
