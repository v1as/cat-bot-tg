package ru.v1as.tg.cat.service.clock;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
public class SimpleThreadSleepClock implements BotClock {

    @Override
    @SneakyThrows
    public void wait(int milliseconds) {
        Thread.sleep(milliseconds);
    }
}
