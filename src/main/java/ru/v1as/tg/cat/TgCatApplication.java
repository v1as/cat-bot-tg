package ru.v1as.tg.cat;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@Slf4j
@ComponentScan
@EnableScheduling
@EnableAsync
@Configuration
public class TgCatApplication implements ApplicationListener<ApplicationStartedEvent> {

    @Autowired private TgBot tgBot;

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(TgCatApplication.class, args);
    }

    @Override
    @SneakyThrows
    public void onApplicationEvent(ApplicationStartedEvent event) {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(tgBot);
        log.info("Bot registered!");
    }
}
