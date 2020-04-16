package ru.v1as.tg.cat.jpa.entities.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ChatUserParam {
    MONEY("0", null),
    WAY_TO_SHOP("false", null);

    private final String defaultValue;
    private final Integer maxValue;

    public boolean inRange(int newValue) {
        return maxValue == null || maxValue >= newValue;
    }
}
