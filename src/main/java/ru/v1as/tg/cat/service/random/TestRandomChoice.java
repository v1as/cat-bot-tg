package ru.v1as.tg.cat.service.random;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
@Profile("test")
public class TestRandomChoice implements RandomChoice {
    @Override
    public <T> T get(RandomRequest<T> request) {
        return request.getItems().iterator().next().getValue();
    }
}
