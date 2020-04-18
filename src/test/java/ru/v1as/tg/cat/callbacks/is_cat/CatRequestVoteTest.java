package ru.v1as.tg.cat.callbacks.is_cat;

import static org.junit.Assert.assertEquals;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT2;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT4;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;

import org.junit.Test;

public class CatRequestVoteTest {

    @Test
    public void test_increment() {
        assertEquals(CAT1, NOT_CAT.increment());
        assertEquals(CAT2, CAT1.increment());
        assertEquals(CAT4, CAT4.increment());
    }
}
