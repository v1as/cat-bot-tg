package ru.v1as.tg.cat.service.clock;

import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestBotClock implements BotClock {

    private final Set<Task> tasks = new TreeSet<>();

    @Override
    public void wait(int milliseconds) {
        // nothing to do here
    }

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        synchronized (this) {
            tasks.add(new Task(runnable, unit.toNanos(delay));
        }
    }

    public void skip(long delay, TimeUnit timeUnit) {
        synchronized (this) {
            final long nanos = timeUnit.toNanos(delay);
            Task todo = null;
            do {
                final Optional<Task> task =
                        tasks.stream().filter(t -> t.getNanos() < nanos).findFirst();
                //todo
            } while (todo != null);
        }
    }

    @Data
    @AllArgsConstructor
    private static class Task implements Comparable<Task> {
        private final Runnable runnable;
        private long nanos;

        void run() {
            this.runnable.run();
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(this.getNanos(), o.getNanos());
        }
    }

}
