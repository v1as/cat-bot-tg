package ru.v1as.tg.cat.service.random;

import java.util.function.Function;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
@Profile("test")
public class TestRandomChoice<L> implements RandomChoice {

    private Function<RandomRequest<L>, L> chooser = r -> r.getItems().iterator().next().getValue();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(RandomRequest<T> request) {
        return (T) chooser.apply((RandomRequest<L>) request);
    }

    public void setChooser(Function<RandomRequest<L>, L> chooser) {
        this.chooser = chooser;
    }
}
