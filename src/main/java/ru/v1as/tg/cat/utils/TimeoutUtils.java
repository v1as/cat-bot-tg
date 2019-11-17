package ru.v1as.tg.cat.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimeoutUtils {

    private static final int MIN_READING_TIMEOUT_MS = 1500;
    private static final int SYMBOL_TIMEOUT_MS = 70;

    public static int getMsForTextReading(int symbols) {
        return Math.max(MIN_READING_TIMEOUT_MS, symbols * SYMBOL_TIMEOUT_MS);
    }
}
