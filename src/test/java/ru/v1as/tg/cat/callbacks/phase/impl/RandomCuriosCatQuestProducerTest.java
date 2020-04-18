package ru.v1as.tg.cat.callbacks.phase.impl;

import static com.google.common.collect.ImmutableMap.of;
import static java.time.LocalDateTime.now;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.collect.ImmutableList;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.junit.Test;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatEventType;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.model.TestTgChat;
import ru.v1as.tg.cat.model.TestTgUser;
import ru.v1as.tg.cat.service.random.TestRandomChoice;

public class RandomCuriosCatQuestProducerTest {

    private TestTgUser user = TestTgUser.tgUser(0);
    private TestTgChat chat = new TestTgChat(true, 0);

    @Test
    public void test_empty_producer() {
        final JustOneCatPhase justOneCatPhase = justOneCatPhase();
        final AbstractCuriosCatPhase phase =
                new RandomCuriosCatQuestProducer(
                                phases(), eventDao(of()), new TestRandomChoice<>(), justOneCatPhase)
                        .get(user, chat);
        assertEquals("JustOneCatPhase", phase.getName());
    }

    @Test
    public void test_filter() {
        final JustOneCatPhase justOneCatPhase = justOneCatPhase();
        final ImmutableList<AbstractCuriosCatPhase> phases =
                ImmutableList.of(
                        phase("phase1", false),
                        phase("phase2", false),
                        phase("phase3", true),
                        phase("phase4", false));
        final AbstractCuriosCatPhase phase =
                new RandomCuriosCatQuestProducer(
                                phases, eventDao(of()), new TestRandomChoice<>(), justOneCatPhase)
                        .get(user, chat);
        assertEquals("phase3", phase.getName());
    }

    @Test
    public void test_recent_played() {
        final JustOneCatPhase justOneCatPhase = justOneCatPhase();

        final LocalDateTime now = now();
        final AbstractCuriosCatPhase phase =
                new RandomCuriosCatQuestProducer(
                                phases("phase1", "phase2", "phase3", "phase4"),
                                eventDao(
                                        of(
                                                "phase1",
                                                now,
                                                "phase2",
                                                now,
                                                "phase3",
                                                now.minusDays(5),
                                                "phase4",
                                                now)),
                                new TestRandomChoice<>(),
                                justOneCatPhase)
                        .get(user, chat);
        assertEquals("phase3", phase.getName());
    }

    private CatUserEventDao eventDao(Map<String, LocalDateTime> events) {
        final CatUserEventDao mock = mock(CatUserEventDao.class);
        final ArrayList<CatUserEvent> mockEvents = new ArrayList<>();
        for (Entry<String, LocalDateTime> e : events.entrySet()) {
            final CatUserEvent event =
                    new CatUserEvent(
                            null, null, 1, CatEventType.CURIOS_CAT_QUEST, CatRequestVote.CAT1);
            event.setQuestName(e.getKey());
            event.setDate(e.getValue());
            mockEvents.add(event);
        }
        when(mock.findByUserId(anyInt())).thenReturn(mockEvents);
        return mock;
    }

    private List<AbstractCuriosCatPhase> phases(String... names) {
        return Arrays.stream(names).map(name -> phase(name, true)).collect(Collectors.toList());
    }

    private AbstractCuriosCatPhase phase(String name, boolean filtered) {
        final AbstractCuriosCatPhase mock = mock(AbstractCuriosCatPhase.class);
        when(mock.getName()).thenReturn(name);
        when(mock.filter(any(), any())).thenReturn(filtered);
        return mock;
    }

    private JustOneCatPhase justOneCatPhase() {
        final JustOneCatPhase mock = mock(JustOneCatPhase.class);
        when(mock.getName()).thenReturn("JustOneCatPhase");
        return mock;
    }
}
