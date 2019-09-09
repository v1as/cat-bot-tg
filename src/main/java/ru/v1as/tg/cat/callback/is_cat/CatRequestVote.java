package ru.v1as.tg.cat.callback.is_cat;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CatRequestVote {
    CAT1(1, "crv1"),
    CAT2(2, "crv2"),
    CAT3(3, "crv3"),
    NOT_CAT(0, "crv0");

    public static String PREFIX = "crv";

    private final int amount;
    private final String callback;

    CatRequestVote(int cats, String callback) {
        this.amount = cats;
        this.callback = callback;
    }

    public static CatRequestVote parse(String data) {
        return Arrays.stream(values())
                .filter(v -> v.callback.equals(data))
                .findFirst()
                .orElse(null);
    }
}
