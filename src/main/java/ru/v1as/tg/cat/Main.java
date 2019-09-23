package ru.v1as.tg.cat;

import static java.util.concurrent.TimeUnit.DAYS;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tasks.CuriosCatRequestScheduler;
import ru.v1as.tg.cat.tasks.RequestsChecker;
import ru.v1as.tg.cat.tasks.SendWinners;

@Slf4j
public class Main {

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(1);

    private static final int FLUSH_FILE_INTERVAL = 60;
    private static final int REQUEST_CHECK_INTERVAL = 2;

    public static void main(String[] args) {
        log.info("Cat bot is starting...");
        try {
            ScoreData scoreData = new ScoreData();
            scoreData.init();
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            CatBot bot = null;

//            EXECUTOR_SERVICE.scheduleWithFixedDelay(
//                    new RequestsChecker(bot, bot.getData(), scoreData),
//                    REQUEST_CHECK_INTERVAL,
//                    REQUEST_CHECK_INTERVAL,
//                    SECONDS);
//            EXECUTOR_SERVICE.scheduleWithFixedDelay(
//                    scoreData::flush, FLUSH_FILE_INTERVAL, FLUSH_FILE_INTERVAL, SECONDS);
//            EXECUTOR_SERVICE.scheduleAtFixedRate(
//                    new SendWinners(bot, bot.getData(), scoreData),
//                    getWinnersSendingInitialDelay(),
//                    DAYS.toSeconds(1),
//                    SECONDS);
            new CuriosCatRequestScheduler(bot.getData(), bot);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Some telegram exception", e);
        }
    }

    private static long getWinnersSendingInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenHours = now.withHour(10).withMinute(0).withSecond(0);
        if (tenHours.isBefore(now)) {
            tenHours = tenHours.plusDays(1);
        }
        log.info("Next time for winners calculation: {}", tenHours);
        return Duration.between(now, tenHours).getSeconds();
    }
}
