package ru.v1as.tg.cat.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.NOT_CAT;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.CANCELED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.FINISHED;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.FORBIDDEN;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.SAME;
import static ru.v1as.tg.cat.callbacks.is_cat.RequestAnswerResult.VOTED;
import static ru.v1as.tg.cat.model.TestTgUser.tgUser;

import org.junit.Test;

public class CatRequestTest {

    @Test
    public void shouldCloseAfterThreeVotes() {
        final CatRequest req = new CatRequest(tgUser(0), 1, 1L);
        req.vote(tgUser(1), CAT1);
        req.vote(tgUser(2), CAT1);
        req.vote(tgUser(3), CAT1);
        assertTrue(req.isClosed());
        assertEquals(CAT1, req.getResult());
    }

    @Test
    public void shouldNotCloseIfSameUserVoted() {
        final CatRequest req = new CatRequest(tgUser(0), 1, 1L);
        assertEquals(VOTED, req.vote(tgUser(1), CAT1));
        assertEquals(VOTED, req.vote(tgUser(2), CAT1));
        assertEquals(SAME, req.vote(tgUser(2), CAT1));
        assertFalse(req.isClosed());
        assertNull(req.getResult());
        assertEquals(2, req.getVotes().size());
    }

    @Test
    public void shouldIgnoreOwnerVote() {
        final CatRequest req = new CatRequest(tgUser(0), 1, 1L);
        assertEquals(FORBIDDEN, req.vote(tgUser(0), CAT1));
        assertFalse(req.isClosed());
        assertNull(req.getResult());
        assertEquals(0, req.getVotes().size());
    }

    @Test
    public void shouldCancelIfOwnerVoteNoCat() {
        final CatRequest req = new CatRequest(tgUser(0), 1, 1L);
        assertEquals(CANCELED, req.vote(tgUser(0), NOT_CAT));
        assertFalse(req.isClosed());
        assertEquals(NOT_CAT, req.getResult());
        assertEquals(0, req.getVotes().size());
    }

    @Test
    public void shouldIgnoreVotesIfClosed() {
        final CatRequest req = new CatRequest(tgUser(0), 1, 1L);
        req.cancel();
        assertEquals(FINISHED, req.vote(tgUser(1), CAT1));
        assertFalse(req.isClosed());
        assertEquals(NOT_CAT, req.getResult());
        assertEquals(0, req.getVotes().size());
    }
}
