package ru.v1as.tg.cat.model.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RandomRequest<T> {

    public static final int DEFAULT_WEIGHT = 10;
    private final List<RandomItem<T>> items = new ArrayList<>();
    private final Set<RandomFlag> flags = new HashSet<>();

    public RandomRequest<T> addAll(T... value) {
        addAll(Arrays.asList(value));
        return this;
    }

    public RandomRequest<T> addAll(Iterable<T> value) {
        value.forEach(this::add);
        return this;
    }

    public RandomRequest<T> add(T value) {
        return add(value, DEFAULT_WEIGHT);
    }

    public RandomRequest<T> add(T value, int weight) {
        this.items.add(new RandomItem<>(value, weight));
        return this;
    }

    public RandomRequest<T> addFlag(RandomFlag flag) {
        this.flags.add(flag);
        return this;
    }

    public List<RandomItem<T>> getItems() {
        return items;
    }

    public Set<RandomFlag> getFlags() {
        return flags;
    }

    public int size() {
        return items.size();
    }
}
