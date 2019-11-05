package ru.v1as.tg.cat.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNoRepeats<T> implements RandomChoice<T> {

    private final Random rnd = new Random();

    private final List<T> values;
    private final List<T> oldValues = new ArrayList<>();

    public RandomNoRepeats(List<T> values) {
        checkArgument(values.size() > 0);
        this.values = new ArrayList<>(values);
    }

    @Override
    public T get() {
        if (values.isEmpty()) {
            values.addAll(oldValues);
            oldValues.clear();
        }
        int i = rnd.nextInt(values.size());
        T value = values.remove(i);
        oldValues.add(value);
        return value;
    }

    @Override
    public Integer size() {
        return values.size() + oldValues.size();
    }
}
