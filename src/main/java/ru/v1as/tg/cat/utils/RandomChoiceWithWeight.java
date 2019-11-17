package ru.v1as.tg.cat.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Value;

public class RandomChoiceWithWeight<T> implements RandomChoice<T> {

    private final List<ValueWeight> values = new ArrayList<>();
    private final Random rnd = new Random();

    public RandomChoiceWithWeight<T> add(T value, int weight) {
        values.add(new ValueWeight(value, weight));
        return this;
    }

    @Override
    public T get() {
        if (values.size() == 0) {
            return null;
        }
        final int summaryWeight = values.stream().mapToInt(ValueWeight::getWeight).sum();
        int random = rnd.nextInt(summaryWeight);
        for (final ValueWeight valueWeight : values) {
            if (valueWeight.getWeight() >= random) {
                return valueWeight.getValue();
            } else {
                random -= valueWeight.getWeight();
            }
        }
        throw new RuntimeException("Unreachable exception");
    }

    @Override
    public int size() {
        return values.size();
    }

    @Value
    private class ValueWeight {
        T value;
        int weight;
    }
}
