package ru.v1as.tg.cat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CatRequestAnswerResult {
    VOTED("Голос учтён"),
    SAME("Вы уже так проголосовали"),
    CHANGED("Голос изменён"),
    FORBIDDEN("Вам запрещено голосовать"),
    CANCELED("Вы закрыли голосование"),
    FINISHED("Заявка закрыта");

    private final String text;
}
