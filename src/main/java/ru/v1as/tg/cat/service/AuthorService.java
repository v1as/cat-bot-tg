package ru.v1as.tg.cat.service;

import static java.util.Collections.emptyList;
import static java.util.Comparator.comparingLong;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.LongProperty;

@Service
@RequiredArgsConstructor
public class AuthorService {
    private final CatUserEventDao catUserEventDao;
    private final ChatDao chatDao;
    private final Properties authors;

    public LongProperty[] getAuthorsStream(Long chatId, LocalDateTime after) {
        final Set<String> chatAuthors =
                chatDao.findById(chatId).map(ChatEntity::getUsers).orElse(emptyList()).stream()
                        .filter(Objects::nonNull)
                        .map(UserEntity::getUserName)
                        .collect(Collectors.toSet());
        return catUserEventDao.findByDateGreaterThan(after).stream()
                .filter(e -> chatAuthors.contains(authors.getProperty(e.getQuestName())))
                .collect(groupingBy(e -> authors.getProperty(e.getQuestName()), counting()))
                .entrySet()
                .stream()
                .sorted(comparingLong(e -> -1 * e.getValue()))
                .map(e -> new LongProperty(e.getKey(), e.getValue()))
                .toArray(LongProperty[]::new);
    }
}
