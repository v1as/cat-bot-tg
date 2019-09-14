package ru.v1as.tg.cat.model;

import lombok.Value;

@Value
public class LongProperty {
    String name;
    Long value;

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
