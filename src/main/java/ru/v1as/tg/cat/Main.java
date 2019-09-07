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

@Slf4j
public class Main {

    private static final ScheduledExecutorService EXECUTOR_SERVICE =
            Executors.newScheduledThreadPool(1);

    private static final int FLUSH_FILE_INTERVAL = 60;
    private static final int REQUEST_CHECK_INTERVAL = 2;

    public static void main(String[] args) {
        try {
            ScoreData scoreData = new ScoreData("cat_scores.txt");
            scoreData.init();
            ApiContextInitializer.init();
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
            CatBot bot = new CatBot(scoreData);

            EXECUTOR_SERVICE.scheduleWithFixedDelay(
                    bot::checkCatRequests, REQUEST_CHECK_INTERVAL, REQUEST_CHECK_INTERVAL, SECONDS);
            EXECUTOR_SERVICE.scheduleWithFixedDelay(
                    scoreData::flush, FLUSH_FILE_INTERVAL, FLUSH_FILE_INTERVAL, SECONDS);
            EXECUTOR_SERVICE.scheduleAtFixedRate(
                    bot::sendDayWinners, getInitialDelay(), DAYS.toSeconds(1), SECONDS);
            telegramBotsApi.registerBot(bot);
        } catch (TelegramApiException e) {
            log.error("Some telegram exception", e);
        }
    }

    private static long getInitialDelay() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenHours = now.withHour(10);
        if (tenHours.isBefore(now)) {
            tenHours = tenHours.plusDays(1);
        }
        return Duration.between(now, tenHours).getSeconds();
    }
}
