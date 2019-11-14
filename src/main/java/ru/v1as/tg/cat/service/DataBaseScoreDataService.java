package ru.v1as.tg.cat.service;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.summarizingInt;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.model.LongProperty;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataBaseScoreDataService implements ScoreDataService {
    private final CatUserEventDao catUserEventDao;

    @Override
    public Stream<LongProperty> getWinnersStream(Long chatId, LocalDateTime after) {
        final List<CatUserEvent> catUserEvents =
                catUserEventDao.findByChatIdAndDateGreaterThan(chatId, after);
        return catUserEvents.stream()
                .collect(
                        groupingBy(
                                (CatUserEvent e) -> e.getUser().getUsernameOrFullName(),
                                summarizingInt(e -> e.getResult().getAmount())))
                .entrySet()
                .stream()
                .sorted(Comparator.comparingLong(e -> -1 * e.getValue().getSum()))
                .map(e -> new LongProperty(e.getKey(), e.getValue().getSum()));
    }
}
