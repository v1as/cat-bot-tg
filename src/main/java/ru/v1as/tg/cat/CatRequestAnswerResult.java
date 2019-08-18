package ru.v1as.tg.cat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CatRequestAnswerResult {
    VOTED("Голос учтён"),
    CHANGED("Голос изменён"),
    FORBIDDEN("Вам запрещено голосовать"),
    CANCELED("Вы закрыли голосование");

    private final String text;
}
