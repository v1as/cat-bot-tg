package ru.v1as.tg.cat.callbacks.is_cat;

import static ru.v1as.tg.cat.EmojiConst.MONEY_BAG;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RequestAnswerResult {
    VOTED("Голос учтён (+1" + MONEY_BAG + ")"),
    SAME("Вы уже так проголосовали"),
    CHANGED("Голос изменён"),
    FORBIDDEN("Вам запрещено голосовать"),
    CANCELED("Вы закрыли голосование"),
    FINISHED("Голосование закрыто");

    private final String text;
}
