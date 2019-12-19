package ru.v1as.tg.cat.model;

import lombok.EqualsAndHashCode;
import lombok.experimental.Delegate;
import org.telegram.telegrambots.meta.api.objects.User;

@EqualsAndHashCode(of = "id")
public class TgUserWrapper implements TgUser {

    @Delegate private final User user;
    private final Integer id;

    private TgUserWrapper(User user) {
        this.user = user;
        this.id = user.getId();
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
