package ru.v1as.tg.cat.callbacks.phase.impl;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.service.random.RandomChoice;

@Slf4j
@Component
@RequiredArgsConstructor
public class RandomCuriosCatQuestProducer implements CuriosCatQuestProducer {

    public static final int QUESTS_AMOUNT_LIMIT = 20;
    private final List<AbstractCuriosCatPhase> nextPhases;
    private final CatUserEventDao catUserEventDao;
    private final RandomChoice randomChoice;
    private final JustOneCatPhase justOneCatPhase;

    @PostConstruct
    public void init() {
        log.info(
                "Curios cat quests amount: '{}' and values: {}",
                nextPhases.size(),
                nextPhases.stream()
                        .map(Object::getClass)
                        .map(Class::getSimpleName)
                        .collect(Collectors.joining("; ", "[", "]")));
    }

    @Override
    public AbstractCuriosCatPhase get(Integer userId) {
        final Map<String, Long> questToAmount =
                nextPhases.stream()
                        .collect(Collectors.toMap(AbstractCuriosCatPhase::getName, p -> 0L));
        catUserEventDao.findByUserId(userId).stream()
                .filter(e -> !isEmpty(e.getQuestName()))
                .collect(Collectors.groupingBy(CatUserEvent::getQuestName, Collectors.counting()))
                .forEach(questToAmount::put);
        final Long rareQuestPlayedAmount =
                questToAmount.values().stream().mapToLong(value -> value).min().orElse(0);
        final List<String> rareQuestName =
                questToAmount.entrySet().stream()
                        .filter(e -> e.getValue().equals(rareQuestPlayedAmount))
                        .map(Entry::getKey)
                        .collect(Collectors.toList());
        if (rareQuestName.size() > QUESTS_AMOUNT_LIMIT) {
            log.info("This player already played too many times all quests.");
            return justOneCatPhase;
        }
        List<AbstractCuriosCatPhase> quests =
                rareQuestName.isEmpty()
                        ? nextPhases
                        : nextPhases.stream()
                                .filter(q -> rareQuestName.contains(q.getName()))
                                .collect(Collectors.toList());
        return randomChoice.random(quests);
    }
}
