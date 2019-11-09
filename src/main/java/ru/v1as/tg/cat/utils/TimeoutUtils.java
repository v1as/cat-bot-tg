package ru.v1as.tg.cat.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class TimeoutUtils {

    public static int getMsForTextReading(int symbols) {
        return Math.max(1500, symbols * 70);
    }
}
