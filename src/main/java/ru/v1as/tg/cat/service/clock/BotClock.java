package ru.v1as.tg.cat.service.clock;

import java.util.concurrent.TimeUnit;

public interface BotClock {

    void wait(int milliseconds);

    void schedule(Runnable o, long amount, TimeUnit minutes);

}
