package ru.v1as.tg.cat.callbacks.is_cat;

import java.util.Arrays;
import lombok.Getter;

@Getter
public enum CatRequestVote {
    NOT_CAT(0, "crv0"),
    CAT1(1, "crv1"),
    CAT2(2, "crv2"),
    CAT3(3, "crv3"),
    CAT4(4, "crv4");

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

    public CatRequestVote increment() {
        int index = Arrays.binarySearch(CatRequestVote.values(), this);
        final int length = values().length;
        return values()[index + 1 == length ? length - 1 : index + 1];
    }
}
