package ru.v1as.tg.cat.service.random;

import java.util.Random;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.random.RandomItem;
import ru.v1as.tg.cat.model.random.RandomRequest;

@Component
@Profile("!test")
public class SimpleRandomChoicer implements RandomChoice {

    private final Random rnd = new Random();

    public SimpleRandomChoicer() {}

    @Override
    public <T> T get(RandomRequest<T> request) {
        if (request.size() == 0) {
            return null;
        }
        final int summaryWeight = request.getItems().stream().mapToInt(RandomItem::getWeight).sum();
        int random = rnd.nextInt(summaryWeight);
        for (final RandomItem<T> valueWeight : request.getItems()) {
            if (valueWeight.getWeight() >= random) {
                return valueWeight.getValue();
            } else {
                random -= valueWeight.getWeight();
            }
        }
        throw new RuntimeException("Unreachable");
    }
}
