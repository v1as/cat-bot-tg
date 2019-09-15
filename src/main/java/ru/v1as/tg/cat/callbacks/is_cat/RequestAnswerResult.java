package ru.v1as.tg.cat.callbacks.is_cat;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestAnswerResult {
    VOTED("Голос учтён"),
    SAME("Вы уже так проголосовали"),
    CHANGED("Голос изменён"),
    FORBIDDEN("Вам запрещено голосовать"),
    CANCELED("Вы закрыли голосование"),
    FINISHED("Голосование закрыто");

    private final String text;
}
