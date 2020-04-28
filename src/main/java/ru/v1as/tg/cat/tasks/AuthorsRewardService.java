package ru.v1as.tg.cat.tasks;

import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.v1as.tg.cat.jpa.dao.CatUserEventDao;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.service.ChatParamResource;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorsRewardService {

    private final CatUserEventDao catUserEventDao;
    private final ChatDao chatDao;
    private final UserDao userDao;
    private final ChatParamResource parameters;
    private final Map<String, UserEntity> questNameToUser = new HashMap<>();
    private final Properties authors;

    @PostConstruct
    public void init() {
        for (Entry<Object, Object> author : authors.entrySet()) {
            final String username = (String) author.getValue();
            final String questName = (String) author.getKey();
            userDao.findByUserName(username)
                    .ifPresent(user -> questNameToUser.put(questName, user));
        }
        log.info("AuthorsRewardService was inited");
    }


    @Scheduled(cron = "0 0 10 * * *")
    public void run() {
        log.info("Start rewarding authors");
        catUserEventDao.findByDateGreaterThan(LocalDateTime.now().minusDays(1)).stream()
                .filter(e -> e.getQuestName() != null)
                .filter(e -> questNameToUser.containsKey(e.getQuestName()))
                .collect(groupingBy(e -> questNameToUser.get(e.getQuestName()), counting()))
                .forEach(this::doRewardUser);
        log.info("Finish rewarding authors");
    }

    private void doRewardUser(UserEntity user, Long amount) {
        try {
            for (ChatEntity chat : chatDao.findByUsersId(user.getId())) {
                parameters.increment(chat, user, ChatUserParam.MONEY, amount.intValue());
            }
        } catch (Exception e) {
            log.error(String.format("Error while rewarding user %s", user), e);
        }
    }
}
