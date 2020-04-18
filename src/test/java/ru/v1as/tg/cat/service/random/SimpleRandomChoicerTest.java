package ru.v1as.tg.cat.service.random;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import ru.v1as.tg.cat.model.random.RandomRequest;

public class SimpleRandomChoicerTest {

    @Test
    public void name() {
        final RandomRequest<Integer> req =
                new RandomRequest<Integer>().add(1).add(2).add(3);
        Set<Integer> result = new HashSet<>();
        for (int i = 0; i < 100; i++) {
            result.add(new SimpleRandomChoicer().get(req));
        }
        assertEquals(3, result.size());
    }
}
