package ru.v1as.tg.cat.service.random;

import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
@Profile("test")
public class TestRandomChoice implements RandomChoice {

    private Function<RandomRequest<?>, ?> chooser = r -> r.getItems().iterator().next().getValue();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(RandomRequest<T> request) {
        return (T) chooser.apply(request);
    }

    public void setChooser(Function<RandomRequest<?>, ?> chooser) {
        this.chooser = chooser;
    }
}
