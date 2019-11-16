package ru.v1as.tg.cat.model;

import static ru.v1as.tg.cat.model.TgChatWrapper.wrap;
import static ru.v1as.tg.cat.model.TgUserWrapper.wrap;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class UpdateUtils {

    public static TgChat getChat(Update update) {
        Chat result = null;
        if (update.hasMessage()) {
            result = update.getMessage().getChat();
        } else if (update.hasCallbackQuery()) {
            result = update.getCallbackQuery().getMessage().getChat();
        } else if (update.hasEditedMessage()) {
            result = update.getEditedMessage().getChat();
        }
        return result != null ? wrap(result) : null;
    }

    public static TgUser getUser(Update update) {
        User from = null;
        if (update.hasMessage()) {
            from = update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            from = update.getCallbackQuery().getFrom();
        } else if (update.hasEditedMessage()) {
            from = update.getEditedMessage().getFrom();
        }
        return from != null ? wrap(from) : null;
    }
}
