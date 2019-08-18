package ru.v1as.tg.cat;

import static java.util.Optional.ofNullable;

import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
class UpdateUtils {

    static Message getMessage(Update update) {
        if (update.hasMessage()) {
            return update.getMessage();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage();
        }
        return null;
    }

    static Chat getChat(Update update) {
        return ofNullable(getMessage(update)).map(Message::getChat).orElse(null);
    }

    static User getUser(Update update) {
        if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        } else if (update.hasMessage()) {
            return update.getMessage().getFrom();
        }
        return null;
    }
}
