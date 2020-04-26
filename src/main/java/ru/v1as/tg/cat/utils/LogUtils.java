package ru.v1as.tg.cat.utils;

import java.util.function.Predicate;
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
            }
        };
    }

    public static <T> Predicate<T> logExceptions(Predicate<T> runnable, boolean defaultValue) {
        return (T t) -> {
            try {
                return runnable.test(t);
            } catch (Exception e) {
                log.error("Error in predicate", e);
                return defaultValue;
            }
        };
    }
}
