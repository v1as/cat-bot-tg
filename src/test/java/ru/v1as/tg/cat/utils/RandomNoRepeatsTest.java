package ru.v1as.tg.cat.utils;

import com.google.common.collect.ImmutableList;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

public class RandomNoRepeatsTest {

    @Test
    public void should_return_values_with_no_repeat() {
        RandomChoice<String> randomChoice =
                new RandomNoRepeats<>(ImmutableList.of("1", "2", "3", "4", "5"));
        Set<String> values = new HashSet<>();
        for (int i = 0; i < 5; i++) {
            values.add(randomChoice.get());
        }
        Assert.assertEquals(5, values.size());
    }
}
