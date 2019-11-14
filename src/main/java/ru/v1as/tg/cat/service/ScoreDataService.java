package ru.v1as.tg.cat.service;

import java.time.LocalDateTime;
import java.util.stream.Stream;
import ru.v1as.tg.cat.model.LongProperty;

public interface ScoreDataService {

    Stream<LongProperty> getWinnersStream(Long chatId, LocalDateTime after);
}
