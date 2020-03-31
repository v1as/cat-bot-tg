package ru.v1as.tg.cat.callbacks.phase.impl;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.Test;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.service.random.SimpleRandomChoicer;

public class CuriosCatQuestProducerTest {

    @Test
    public void shouldReturnQuestIfNoPlayerQuests() {
        final AbstractCuriosCatPhase phase =
                new CuriosCatQuestProducer(
                                of(new TestPhase("quest1")),
                                getCatUserEventDaoMock(),
                                new SimpleRandomChoicer(),
                                new JustOneCatPhase())
                        .get(0);
        assertEquals(phase.getName(), "quest1");
    }

    @Test
    public void shouldReturnNonPlayerQuest() {
        final AbstractCuriosCatPhase phase =
                new CuriosCatQuestProducer(
                                of(new TestPhase("quest1"), new TestPhase("quest2")),
                                getCatUserEventDaoMock("quest2"),
                                new SimpleRandomChoicer(),
                                new JustOneCatPhase())
                        .get(0);
        assertEquals(phase.getName(), "quest1");
    }

    @Test
    public void shouldReturnRareQuest() {
        final AbstractCuriosCatPhase phase =
                new CuriosCatQuestProducer(
                                of(
                                        new TestPhase("questY"),
                                        new TestPhase("questX"),
                                        new TestPhase("questZ")),
                                getCatUserEventDaoMock(
                                        "questY", "questY", "questZ", "questX", "questX"),
                                new SimpleRandomChoicer(),
                                new JustOneCatPhase())
                        .get(0);
        assertEquals(phase.getName(), "questZ");
    }

    private CatUserEventDao getCatUserEventDaoMock(String... names) {
        final CatUserEventDao dao = mock(CatUserEventDao.class);
        final List<CatUserEvent> quests =
                Arrays.stream(names)
                        .map(
                                name -> {
                                    CatUserEvent event = new CatUserEvent();
                                    event.setQuestName(name);
                                    return event;
                                })
                        .collect(Collectors.toList());
        when(dao.findByUserId(anyInt())).thenReturn(quests);
        return dao;
    }

    private static class TestPhase extends AbstractCuriosCatPhase {

        private final String testName;

        TestPhase(String testName) {
            this.testName = testName;
        }

        @Override
        protected void open() {}

        @Override
        public String getName() {
            return testName;
        }
    }
}
