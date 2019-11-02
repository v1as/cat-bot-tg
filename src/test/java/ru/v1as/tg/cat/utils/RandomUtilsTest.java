package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static ru.v1as.tg.cat.utils.RandomUtils.random;

import java.util.Arrays;
import org.junit.Test;

public class RandomUtilsTest {

    @Test
    public void name() {
        assertEquals("a", random("a", "a", "a", "a"));
        assertEquals("a", random(Arrays.asList("a", "a", "a", "a")));
        assertEquals("a", random(new String[] {"a", "a", "a", "a"}));
    }
}
