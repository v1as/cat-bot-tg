package ru.v1as.tg.cat.utils;

import java.util.function.Predicate;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PredicateUtils {

    public static <T> Predicate<T> not(Predicate<T> predicate) {
        return (v) -> !predicate.test(v);
    }
}
