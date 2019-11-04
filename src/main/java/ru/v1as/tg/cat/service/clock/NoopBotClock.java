package ru.v1as.tg.cat.service.clock;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class NoopBotClock implements BotClock {

    @Override
    public void wait(int milliseconds) {
        // nothing to do here
    }
}
