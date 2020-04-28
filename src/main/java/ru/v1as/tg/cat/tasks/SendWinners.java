package ru.v1as.tg.cat.tasks;

import static java.time.LocalDateTime.now;
import static java.util.function.Function.identity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.MedalsListBuilder;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.service.AuthorService;
import ru.v1as.tg.cat.service.ScoreDataService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendWinners {

    private final TgSender sender;
    private final ChatDao chatDao;
    private final ChatDetailsDao chatDetailsDao;
    private final ScoreDataService scoreData;
    private final AuthorService authorService;

    @PostConstruct
    public void init() {
        log.info("Winners sender was inited");
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void run() {
        log.info("Start sending winners data...");
        LocalDateTime yesterday = now().minusDays(1);
        final Map<Long, ChatDetailsEntity> id2Details =
                chatDetailsDao.findAll().stream()
                        .collect(Collectors.toMap(ChatDetailsEntity::getId, identity()));
        for (ChatEntity chat : chatDao.findAll()) {
            final ChatDetailsEntity details = id2Details.get(chat.getId());
            if (!details.isEnabled()) {
                log.debug("Chat '{}' skipped because of disabled", chat);
                continue;
            }
            try {
                StringBuilder text = new StringBuilder();
                LongProperty[] topPlayers =
                        scoreData
                                .getWinnersStream(chat.getId(), yesterday)
                                .filter(LongProperty::isPositive)
                                .toArray(LongProperty[]::new);
                final LongProperty[] authors = authorService.getAuthorsStream(chat.getId(), yesterday);
                List<String> result =
                        new MedalsListBuilder().getPlayersWithMedals(topPlayers, authors);

                if (result.isEmpty()) {
                    continue;
                }
                for (String s : result) {
                    text.append(s).append('\n');
                }
                sender.execute(new SendMessage().setChatId(chat.getId()).setText(text.toString()));
                log.info("Winners data '{}' was sent to chat {}", text, chat);
            } catch (Exception ex) {
                log.error("Error while send winners to the chat " + chat, ex);
            }
        }
    }
}
