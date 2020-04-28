package ru.v1as.tg.cat;

import java.util.Properties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.commands.TgCommandProcessorByName;
import ru.v1as.tg.cat.config.JpaConfiguration;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.messages.TgMessageProcessor;
import ru.v1as.tg.cat.service.ChatService;
import ru.v1as.tg.cat.service.clock.BotClock;
import ru.v1as.tg.cat.service.clock.TestBotClock;
import ru.v1as.tg.cat.tg.TgSender;

@Configuration
@ComponentScan({
    "ru.v1as.tg.cat.messages",
    "ru.v1as.tg.cat.callbacks",
    "ru.v1as.tg.cat.tasks",
    "ru.v1as.tg.cat.commands",
    "ru.v1as.tg.cat.service",
    "ru.v1as.tg.cat.callbacks.phase",
})
@Import(JpaConfiguration.class)
public class CaBotTestConfiguration {

    @Bean
    @Primary
    public TgSender tgSender() {
        return new TestAbsSender();
    }

    @Bean
    public CatBotData getDbData() {
        return new CatBotData();
    }

    @Bean
    public Properties authors() {
        return new Properties();
    }

    @Bean
    public CatBot getCatBot(
            TgUpdateBeforeHandler updateBeforeHandler,
            TgCallbackProcessor callbackProcessor,
            TgCommandProcessorByName commandProcessor,
            TgMessageProcessor messageProcessor) {
        return new CatBot(
                updateBeforeHandler, callbackProcessor, commandProcessor, messageProcessor);
    }

    @Bean
    @Primary
    public BotClock getClock() {
        return new TestBotClock();
    }

    @Bean
    @Primary
    public DatabaseUpdateBeforeHandler getDatabaseUpdateBeforeHandler(
            UserDao userDao,
            ChatDao chatDao,
            ChatDetailsDao chatDetailsDao,
            ChatService chatService) {
        return new DatabaseUpdateBeforeHandler(userDao, chatDao, chatDetailsDao, chatService);
    }
}
