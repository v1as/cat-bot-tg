package ru.v1as.tg.cat;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

import java.util.Objects;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.User;

@Data
@FieldDefaults(level = PRIVATE)
@Slf4j
public class UserData {
    Integer id;
    String fullName;
    String userName;
    int reputation = 0;
    int catsSeen = 0;

    UserData(User user) {
        this.id = user.getId();
        log.info("User created: " + user);
        update(user);
    }

    void update(User user) {
        this.fullName =
                String.format(
                        "%s %s",
                        ofNullable(user.getFirstName()).orElse(""),
                        ofNullable(user.getLastName()).orElse(""));
        this.userName = user.getUserName();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        UserData userData = (UserData) o;
        return Objects.equals(id, userData.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
