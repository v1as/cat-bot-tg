package ru.v1as.tg.cat.model;

import static org.apache.http.util.TextUtils.isEmpty;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface TgUser extends Comparable<TgUser> {

    Integer getId();

    String getUserName();

    String getFirstName();

    String getLastName();

    String getLanguageCode();

    default boolean isChatAdmin() {
        return true; // todo implement it with chat relations or tg request
    }

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

    @Override
    default int compareTo(TgUser o) {
        return Integer.compare(this.getId(), o.getId());
    }
}
