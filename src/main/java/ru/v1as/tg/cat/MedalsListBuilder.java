package ru.v1as.tg.cat;

import static ru.v1as.tg.cat.EmojiConst.FIRST_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.SECOND_PLACE_MEDAL;
import static ru.v1as.tg.cat.EmojiConst.THIRD_PLACE_MEDAL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import ru.v1as.tg.cat.model.LongProperty;

public class MedalsListBuilder {

    private static final String[] MEDALS =
            new String[] {FIRST_PLACE_MEDAL, SECOND_PLACE_MEDAL, THIRD_PLACE_MEDAL};

    public List<String> getPlayersWithMedals(LongProperty[] topPlayers, LongProperty[] authors) {
        List<String> result = new ArrayList<>();
        LongProperty last = null;
        final Map<String, Long> authorRewards =
                Arrays.stream(authors)
                        .collect(Collectors.toMap(LongProperty::getName, LongProperty::getValue));
        final Set<String> authorPlayers = new HashSet<>();
        int medalIndex = 0;
        for (LongProperty player : topPlayers) {
            if (last != null && !Objects.equals(last.getValue(), player.getValue())) {
                medalIndex++;
            }
            String medal = medalIndex < MEDALS.length ? MEDALS[medalIndex] : "";
            String author = "";
            if (authorRewards.containsKey(player.getName())) {
                author = authorRewardSuffix(authorRewards.get(player.getName()));
                authorPlayers.add(player.getName());
            }
            result.add(medal + player.toString() + author);
            last = player;
        }
        for (LongProperty authorReward : authors) {
            if (!authorPlayers.contains(authorReward.getName())) {
                result.add(authorReward.getName() + authorRewardSuffix(authorReward.getValue()));
            }
        }
        return result;
    }

    private String authorRewardSuffix(Long reward) {
        return String.format("   [\uD83D\uDCD6 +%s \uD83D\uDCB0]", reward);
    }
}
