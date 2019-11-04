package ru.v1as.tg.cat.model;

import static org.apache.http.util.TextUtils.isEmpty;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class UpdateUtils {

    public static Chat getChat(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getChat();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getChat();
        } else if (update.hasEditedMessage()) {
            return update.getEditedMessage().getChat();
        }
        return null;
    }

    public static User getUser(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getFrom();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getFrom();
        } else if (update.hasEditedMessage()) {
            return update.getEditedMessage().getFrom();
        }
        return null;
    }

    public static String getUsernameOrFullName(User user) {
        if (isEmpty(user.getUserName())) {
            return Stream.of(user.getFirstName(), user.getLastName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
        } else {
            return "@" + user.getUserName();
        }
    }

}
