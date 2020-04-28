package ru.v1as.tg.cat;

import static com.google.common.collect.ImmutableList.of;
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
        List<String> medals =
                medalsListBuilder.getPlayersWithMedals(props(2, 2), new LongProperty[] {});
        medals.forEach(m -> assertTrue(m.startsWith(FIRST_PLACE_MEDAL)));
        assertEquals(2, medals.size());
    }

    @Test
    public void fivePlayersShouldHaveFirstPlayer() {
        List<String> medals =
                medalsListBuilder.getPlayersWithMedals(props(2, 2, 2, 2, 2), new LongProperty[] {});
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
        List<String> medals =
                medalsListBuilder.getPlayersWithMedals(props(1, 2, 3), new LongProperty[] {});
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertEquals(3, medals.size());
    }

    @Test
    public void sixPeopleWithRepeatsScoreShouldHave3TypeMedals() {
        List<String> medals =
                medalsListBuilder.getPlayersWithMedals(
                        props(6, 6, 5, 5, 1, 1), new LongProperty[] {});
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
        List<String> medals =
                medalsListBuilder.getPlayersWithMedals(props(1, 2, 3, 4), new LongProperty[] {});
        assertTrue(medals.get(0).startsWith(FIRST_PLACE_MEDAL));
        assertTrue(medals.get(1).startsWith(SECOND_PLACE_MEDAL));
        assertTrue(medals.get(2).startsWith(THIRD_PLACE_MEDAL));
        assertTrue(medals.get(3).startsWith("1"));
        assertEquals(4, medals.size());
    }

    @Test
    public void playersAndAuthors() {
        final List<String> result =
                medalsListBuilder.getPlayersWithMedals(
                        props2("p1", 3L, "p2", 3L, "p3", 3L), props2("p2", 3L, "p4", 3L));
        assertEquals(4, result.size());
        assertEquals(
                of(
                        "\uD83E\uDD47p1: 3",
                        "\uD83E\uDD47p2: 3   [\uD83D\uDCD6 +3 \uD83D\uDCB0]",
                        "\uD83E\uDD47p3: 3",
                        "p4   [\uD83D\uDCD6 +3 \uD83D\uDCB0]"),
                result);
    }
    @Test
    public void playersAndAfterAuthors() {
        final List<String> result =
            medalsListBuilder.getPlayersWithMedals(
                props2("p1", 3L, "p2", 3L), props2("p0", 3L, "pa", 3L));
        assertEquals(4, result.size());
        assertEquals(
                of(
                        "\uD83E\uDD47p1: 3",
                        "\uD83E\uDD47p2: 3",
                        "p0   [\uD83D\uDCD6 +3 \uD83D\uDCB0]",
                        "pa   [\uD83D\uDCD6 +3 \uD83D\uDCB0]"),
                result);
    }
    private LongProperty[] props2(Object... data) {
        final int length = data.length;
        assertEquals(0, length % 2);
        LongProperty[] result = new LongProperty[length / 2];
        for (int i = 0; i < length / 2; i++) {
            result[i] = new LongProperty((String) data[2 * i], (long) data[2 * i + 1]);
        }
        return result;
    }
}
