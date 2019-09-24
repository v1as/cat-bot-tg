package ru.v1as.tg.cat;

import static org.mockito.Mockito.mock;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.commands.TgCommandProcessor;
import ru.v1as.tg.cat.messages.TgMessageProcessor;
import ru.v1as.tg.cat.model.ScoreData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Configuration
@ComponentScan({"ru.v1as.tg.cat.messages", "ru.v1as.tg.cat.callbacks", "ru.v1as.tg.cat.commands"})
@ActiveProfiles("test")
public class CaBotTestConfiguration {

    @Bean
    @Primary
    public UnsafeAbsSender unsafeAbsSender() {
        return new TestAbsSender();
    }

    @Bean
    public CatBotData getDbData(ScoreData scoreData) {
        return new CatBotData(scoreData);
    }

    @Bean
    public ScoreData getScoreData() {
        return mock(ScoreData.class);
    }

    @Bean
    public CatBot getCatBot(
            CatBotData catBotData,
            TgCallbackProcessor callbackProcessor,
            TgCommandProcessor commandProcessor,
            TgMessageProcessor messageProcessor) {
        return new CatBot(catBotData, callbackProcessor, commandProcessor, messageProcessor);
    }

}
