package ru.v1as.tg.cat;

import java.util.Arrays;
import lombok.Getter;

@Getter
enum CatRequestVote {
    CAT1(1, "1cr"),
    CAT2(2, "2cr"),
    CAT3(3, "3cr"),
    NOT_CAT(0, "0cr");

    private final int amount;
    private final String callback;

    CatRequestVote(int cats, String callback) {
        this.amount = cats;
        this.callback = callback;
    }

    static CatRequestVote parse(String data) {
        return Arrays.stream(values())
                .filter(v -> v.callback.equals(data))
                .findFirst()
                .orElse(null);
    }
}
