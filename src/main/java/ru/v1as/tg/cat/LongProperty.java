package ru.v1as.tg.cat;

import lombok.Value;

@Value
class LongProperty {
    String name;
    Long value;

    @Override
    public String toString() {
        return name + ": " + value;
    }
}
