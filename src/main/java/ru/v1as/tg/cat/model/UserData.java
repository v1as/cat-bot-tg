package ru.v1as.tg.cat.model;

import static java.util.Optional.ofNullable;
import static org.apache.http.util.TextUtils.isEmpty;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.User;

@Data
@EqualsAndHashCode(of = "id")
@Slf4j
public class UserData {
    protected Integer id;
    protected String fullName;
    protected String userName;
    protected Long chatId;

    public UserData(User user) {
        this.id = user.getId();
        log.info("User created: " + user);
        update(user);
    }

    public void update(User user) {
        this.fullName =
                String.format(
                        "%s %s",
                        ofNullable(user.getFirstName()).orElse(""),
                        ofNullable(user.getLastName()).orElse(""));
        this.userName = user.getUserName();
    }

    public String getUsernameOrFullName() {
        if (isEmpty(userName)) {
            return fullName;
        } else {
            return "@" + userName;
        }
    }
}
