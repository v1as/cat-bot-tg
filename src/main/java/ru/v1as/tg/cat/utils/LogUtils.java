package ru.v1as.tg.cat.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@UtilityClass
public class LogUtils {
    public static Runnable logExceptions(Runnable runnable) {
        return () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                log.error("Error in runnable", e);
                throw e;
            }
        };
    }
}
