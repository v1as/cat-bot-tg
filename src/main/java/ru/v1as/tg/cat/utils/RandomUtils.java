package ru.v1as.tg.cat.utils;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;

public class RandomUtils {

    private static final Random RAND = new Random();

    public static <T> T random(List<T> values) {
        return (T) values.get(RAND.nextInt(values.size()));
    }

    @SuppressWarnings("unchecked")
    public static <T> T random(T... values) {
        if (values.length == 1 && values[0] instanceof Iterable) {
            ImmutableList valueList = ImmutableList.copyOf((Iterable) values[0]);
            return (T) valueList.get(RAND.nextInt(valueList.size()));
        } else {
            return (T) values[RAND.nextInt(values.length)];
        }
    }
}
