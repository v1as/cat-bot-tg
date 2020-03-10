package ru.v1as.tg.cat.callbacks.phase.multi_curios.regata;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.v1as.tg.cat.callbacks.phase.multi_curios.UserMarker;

@Getter
@RequiredArgsConstructor
public enum BoatPosition implements UserMarker {
    ON_TAIL(
            new String[] {"Вы удобно устраиваетесь на хвосте.", "Сегодня вы будете рулить яхтой."},
            " сегодня рулевой."),
    ON_GROTTO(
            new String[] {
                "Вы размещаетесь на борту, немного тесновато, но это не так важно.",
                "Над головой трепещет белоснежный парус - грот."
            },
            " сегодня на гроте."),
    ON_STAY_SAIL(
            new String[] {
                "Вы размещаетесь на носу, ",
                "Ветер встречает вас нежным поцелуем.",
                "Команда позади вас вселяет в вас уверенность."
            },
            " сегодня на стакселе.");

    private final String[] userMessages;
    private final String publicMessages;
}
