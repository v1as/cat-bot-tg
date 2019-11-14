package ru.v1as.tg.cat.model;

import static org.apache.http.util.TextUtils.isEmpty;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TgUser {

    Integer getId();

    String getUserName();

    String getFirstName();

    String getLastName();

    String getLanguageCode();

    default String getUsernameOrFullName() {
        if (isEmpty(getUserName())) {
            return Stream.of(getFirstName(), getLastName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
        } else {
            return "@" + getUserName();
        }
    }

    default String getFullName() {
        return Stream.of(getFirstName(), getLastName())
                .filter(Objects::nonNull)
                .collect(Collectors.joining(" "));
    }
}
