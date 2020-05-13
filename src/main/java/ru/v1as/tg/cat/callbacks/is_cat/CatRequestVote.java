package ru.v1as.tg.cat.callbacks.is_cat;

import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CatRequestVote {
    NOT_CAT(0, "crv0", "Любопытный Кот сбегает от игрока "),
    CAT1(1, "crv1", "Любопытный Кот убегает к "),
    CAT2(2, "crv2", "Два кота засчитано игроку "),
    CAT3(3, "crv3", "Целых три кота засчитано игроку "),
    CAT4(4, "crv4", "Целых 4 кота засчитано игроку ");

    public static final int CAT_REWARD = 3;
    public static String PREFIX = "crv";

    private final int amount;
    private final String callback;
    private final String message;

    public static CatRequestVote parse(String data) {
        return Arrays.stream(values())
                .filter(v -> v.callback.equals(data))
                .findFirst()
                .orElse(null);
    }

    public int reward() {
        return amount * CAT_REWARD;
    }

    public String getMessage(String user) {
        return message + user + (amount > 0 ? " (+" + amount * CAT_REWARD + MONEY_BAG + ")" : "");
    }

    public CatRequestVote increment() {
        int index = Arrays.binarySearch(CatRequestVote.values(), this);
        final int length = values().length;
        return values()[index + 1 == length ? length - 1 : index + 1];
    }

    public static CatRequestVote fromAmount(int amount) {
        if (amount == 0) {
            return NOT_CAT;
        } else if (amount == 1) {
            return CAT1;
        } else if (amount == 2) {
            return CAT2;
        } else if (amount == 3) {
            return CAT3;
        } else if (amount == 4) {
            return CAT4;
        } else {
            throw new IllegalArgumentException();
        }
    }
}
