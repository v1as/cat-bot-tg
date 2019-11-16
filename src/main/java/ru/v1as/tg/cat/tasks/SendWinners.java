package ru.v1as.tg.cat.tasks;

import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.MedalsListBuilder;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.model.LongProperty;
import ru.v1as.tg.cat.service.ScoreDataService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendWinners {

    private final TgSender sender;
    private final CatBotData data;
    private final ScoreDataService scoreData;

    @PostConstruct
    public void init() {
        log.info("Winners sender was inited");
    }

    @Scheduled(cron = "0 0 10 * * *")
    public void run() {
        log.info("Start sending winners data...");
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        for (ChatData chat : data.getChats()) {
            try {
                StringBuilder text = new StringBuilder();
                LongProperty[] topPlayers =
                        scoreData
                                .getWinnersStream(chat.getChatId(), yesterday)
                                .filter(LongProperty::isPositive)
                                .toArray(LongProperty[]::new);
                List<String> result = new MedalsListBuilder().getPlayersWithMedals(topPlayers);

                if (result.isEmpty()) {
                    continue;
                }
                for (String s : result) {
                    text.append(s).append('\n');
                }
                sender.execute(
                        new SendMessage().setChatId(chat.getChatId()).setText(text.toString()));
                log.info("Winners data '{}' was sent to chat {}", text, chat);
            } catch (Exception ex) {
                log.error("Error while send winners to the chat " + chat, ex);
            }
        }
    }
}
