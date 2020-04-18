package ru.v1as.tg.cat.callbacks.phase.impl;

import static org.springframework.util.StringUtils.isEmpty;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.AbstractCuriosCatPhase;
import ru.v1as.tg.cat.callbacks.phase.curios_cat.JustOneCatPhase;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
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
    public AbstractCuriosCatPhase get(TgUser user, TgChat chat) {
        final List<CatUserEvent> catEvents = catUserEventDao.findByUserId(user.getId());
        final Set<String> recentlyPlayed = getRecentlyPlayed(catEvents);
        final Map<String, Long> questToAmount =
                getQuestToAmount(user, chat, catEvents, recentlyPlayed);
        final long rareQuestPlayedAmount =
                questToAmount.values().stream().mapToLong(value -> value).min().orElse(0L);
        if (rareQuestPlayedAmount > QUESTS_AMOUNT_LIMIT) {
            log.info("This player already played too many times all quests.");
            return justOneCatPhase;
        }
        final List<String> rareQuestName =
                questToAmount.entrySet().stream()
                        .filter(e -> e.getValue().equals(rareQuestPlayedAmount))
                        .map(Entry::getKey)
                        .collect(Collectors.toList());
        List<AbstractCuriosCatPhase> quests =
                rareQuestName.isEmpty()
                        ? nextPhases
                        : nextPhases.stream()
                                .filter(q -> rareQuestName.contains(q.getName()))
                                .collect(Collectors.toList());
        if (quests.isEmpty()) {
            return justOneCatPhase;
        }
        return randomChoice.random(quests);
    }

    private Set<String> getRecentlyPlayed(List<CatUserEvent> catEvents) {
        final LocalDateTime now = LocalDateTime.now();
        return catEvents.stream()
                .filter(e -> e.getDate().plusDays(3).isAfter(now))
                .map(CatUserEvent::getQuestName)
                .collect(Collectors.toSet());
    }

    private Map<String, Long> getQuestToAmount(
            TgUser user, TgChat chat, List<CatUserEvent> catEvents, Set<String> recentlyPlayed) {
        final Map<String, Long> questToAmount =
                nextPhases.stream()
                        .filter(p -> p.filter(user, chat))
                        .filter(e -> !recentlyPlayed.contains(e.getName()))
                        .collect(Collectors.toMap(AbstractCuriosCatPhase::getName, p -> 0L));
        final Set<String> quests = questToAmount.keySet();
        catEvents.stream()
                .filter(e -> !isEmpty(e.getQuestName()))
                .filter(e -> quests.contains(e.getQuestName()))
                .collect(Collectors.groupingBy(CatUserEvent::getQuestName, Collectors.counting()))
                .forEach(questToAmount::put);
        return questToAmount;
    }
}
