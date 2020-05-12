package ru.v1as.tg.cat;

import java.time.Duration;

public interface ChatReadingDelay {

    default Duration getReadingDelay(Long chatId) {
        return getAndIncreaseReadingDelay(chatId, 0);
    }

    Duration getAndIncreaseReadingDelay(Long chatId, int length);
}
