package ru.v1as.tg.cat;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;
import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ru.v1as.tg.cat.model.LongProperty;

public class MedalsListBuilderTest {

    private MedalsListBuilder medalsListBuilder = new MedalsListBuilder();

    @Test
    public void twoPlayersShouldHaveFirstPlayer() {
        List<String> medals = medalsListBuilder.getPlayersWithMedals(props(2, 2));
        medals.forEach(m -> assertTrue(m.startsWith(FIRST_PLACE_MEDAL)));
        assertEquals(2, medals.size());
    }

    @Test
    public void fivePlayersShouldHaveFirstPlayer() {
        List<String> medals = medalsListBuilder.getPlayersWithMedals(props(2, 2, 2, 2, 2));
        medals.forEach(m -> assertTrue(m.startsWith(FIRST_PLACE_MEDAL)));
        assertEquals(5, medals.size());
    }

    private LongProperty[] props(long... values) {
        return Arrays.stream(values).mapToObj(this::prop).toArray(LongProperty[]::new);
    }

    private LongProperty prop(long value) {
        return new LongProperty("1", value);
    }

    @Test
    public void threePlayersShouldHaveThreeMedals() {
        List<String> medals = medalsListBuilder.getPlayersWithMedals(props(1, 2, 3));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(3, medals.size());
    }

    @Test
    public void sixPeopleWithRepeatsScoreShouldHave3TypeMedals() {
        List<String> medals = medalsListBuilder.getPlayersWithMedals(props(6, 6, 5, 5, 1, 1));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(3).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(4).startsWith(THIRD_PLACE_MEDAL));
        assertTrue(medals.get(5).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(6, medals.size());
    }

    @Test
    public void onlyThreePeopleFrom4ShouldHaveMedals() {
        List<String> medals = medalsListBuilder.getPlayersWithMedals(props(1, 2, 3, 4));
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertTrue(medals.get(3).startsWith("1"));
        assertEquals(4, medals.size());
    }
}
