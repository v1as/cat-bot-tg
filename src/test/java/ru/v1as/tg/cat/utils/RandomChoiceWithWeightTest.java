package ru.v1as.tg.cat.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class RandomChoiceWithWeightTest {

    @Test
    public void shouldReturnNullIfEmpty() {
        final RandomChoiceWithWeight<Integer> empty = new RandomChoiceWithWeight<>();
        assertNull(empty.get());
        assertEquals(0, empty.size());
    }

    @Test
    public void shouldWorkWithCoupleValues() {
        final RandomChoiceWithWeight<String> random =
                new RandomChoiceWithWeight<String>().add("first", 5).add("second", 5);
        assertEquals(2, random.size());
        final String value = random.get();
        assertTrue("first".equals(value) || "second".equals(value));
    }

}
