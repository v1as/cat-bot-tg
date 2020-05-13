package ru.v1as.tg.cat.service.clock;

import static java.util.concurrent.TimeUnit.NANOSECONDS;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

public interface BotClock {

    void schedule(Runnable task, long amount, TimeUnit minutes);

    default void schedule(Runnable task, Duration duration) {
        schedule(task, duration.toNanos(), NANOSECONDS);
    }

}
