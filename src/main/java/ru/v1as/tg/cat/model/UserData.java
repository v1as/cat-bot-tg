package ru.v1as.tg.cat.model;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.Objects;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.User;

@Data
@EqualsAndHashCode(of = "id")
@FieldDefaults(level = PRIVATE)
@Slf4j
public class UserData {
    Integer id;
    String fullName;
    String userName;

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

}
