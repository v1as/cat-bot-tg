package ru.v1as.tg.cat;

import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import ru.v1as.tg.cat.model.LongProperty;

public class MedalsListBuilder {

    private static final String[] MEDALS =
            new String[] {FIRST_PLACE_MEDAL, SECOND_PLACE_MEDAL, THIRD_PLACE_MEDAL};

    public List<String> getPlayersWithMedals(LongProperty[] topPlayers) {
        List<String> result = new ArrayList<>();
        LongProperty last = null;
        int medalIndex = 0;
        for (LongProperty player : topPlayers) {
            if (last != null && !Objects.equals(last.getValue(), player.getValue())) {
                medalIndex++;
            }
            String medal = medalIndex < MEDALS.length ? MEDALS[medalIndex] : "";
            result.add(medal + player.toString());
            last = player;
        }
        return result;
    }
}
