package ru.v1as.tg.cat.callbacks.phase.impl;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.junit.Test;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;

public class CuriosCatQuestProducerTest {

    @Test
    public void shouldReturnQuestIfNoPlayerQuests() {
        final AbstractCuriosCatPhase phase =
                new CuriosCatQuestProducer(of(new TestPhase("quest1")), getCatUserEventDaoMock())
                        .get(0);
        assertEquals(phase.getName(), "quest1");
    }

    @Test
    public void shouldReturnNonPlayerQuest() {
        final AbstractCuriosCatPhase phase =
                new CuriosCatQuestProducer(
                                of(new TestPhase("quest1"), new TestPhase("quest2")),
                                getCatUserEventDaoMock("quest2"))
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
                                        "questY", "questY", "questZ", "questX", "questX"))
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

    @RequiredArgsConstructor
    private static class TestPhase extends AbstractCuriosCatPhase {

        private final String testName;

        @Override
        protected void open() {}

        @Override
        public String getName() {
            return testName;
        }
    }
}
