package ru.v1as.tg.cat.service.random;

import java.util.function.Predicate;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.random.RandomItem;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
@Profile("test")
public class TestRandomChoice<L> implements RandomChoice {

    private Predicate chooser = v -> true;

    @Override
    @SuppressWarnings("unchecked")
    @SneakyThrows
    public <T> T get(RandomRequest<T> request) {
        return (T)
                request.getItems().stream()
                        .map(RandomItem::getValue)
                        .filter(chooser)
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Bad choose predicate"));
    }

    public void setChooser(Predicate<?> chooser) {
        this.chooser = chooser;
    }
}
