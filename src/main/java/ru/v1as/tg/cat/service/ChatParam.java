package ru.v1as.tg.cat.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChatParam {
    PICTURE_POLL_ENABLED("false", null),
    CAT_BITE_LEVEL("0", 5);

    private String defaultValue;
    private Integer maxValue;

    public boolean inRange(int newValue) {
        return maxValue == null || maxValue >= newValue;
    }
}
