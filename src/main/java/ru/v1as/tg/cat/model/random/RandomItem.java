package ru.v1as.tg.cat.model.random;

import lombok.Value;

@Value
public class RandomItem<T> {
    private final T value;
    private final Number weight;
}
