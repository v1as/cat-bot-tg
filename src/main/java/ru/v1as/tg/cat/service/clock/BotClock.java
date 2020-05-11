package ru.v1as.tg.cat.service.clock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface BotClock {

    void schedule(Runnable o, long amount, TimeUnit minutes);

    default void schedule(Runnable o, Duration duration) {
        schedule(o, duration.toNanos(), NANOSECONDS);
    }

}
