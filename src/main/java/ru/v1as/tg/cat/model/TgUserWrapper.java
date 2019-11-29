package ru.v1as.tg.cat.model;

import lombok.experimental.Delegate;
import org.telegram.telegrambots.meta.api.objects.User;

public class TgUserWrapper implements TgUser {

    @Delegate private final User user;

    private TgUserWrapper(User user) {
        this.user = user;
    }

    public static TgUser wrap(User user) {
        return new TgUserWrapper(user);
    }

    @Override
    public String toString() {
        return String.format(
                "User(%s:%s:%s)", this.getUserName(), this.getFullName(), this.getId());
    }
}
