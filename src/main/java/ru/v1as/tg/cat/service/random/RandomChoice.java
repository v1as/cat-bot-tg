package ru.v1as.tg.cat.service.random;

import ru.v1as.tg.cat.model.random.RandomRequest;

public interface RandomChoice {

    <T> T get(RandomRequest<T> request);

    default <T> T random(Iterable<T> quests) {
        return get(new RandomRequest<T>().addAll(quests));
    }

    default <T> T random(T... quests) {
        return get(new RandomRequest<T>().addAll(quests));
    }
}
