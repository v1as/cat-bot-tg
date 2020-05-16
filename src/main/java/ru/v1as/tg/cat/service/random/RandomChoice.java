package ru.v1as.tg.cat.service.random;

import ru.v1as.tg.cat.model.random.RandomRequest;

public interface RandomChoice {

    <T> T get(RandomRequest<T> request);

    default <T> T random(Iterable<T> values) {
        return get(new RandomRequest<T>().addAll(values));
    }

    default <T> T random(T... values) {
        return get(new RandomRequest<T>().addAll(values));
    }
}
