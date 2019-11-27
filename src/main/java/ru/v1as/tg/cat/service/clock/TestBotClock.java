package ru.v1as.tg.cat.service.clock;

import java.util.HashSet;
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
        synchronized (tasks) {
            tasks.add(new Task(runnable, unit.toNanos(delay)));
        }
    }

    public void skip(long delay, TimeUnit timeUnit) {
        final long nanos = timeUnit.toNanos(delay);
        Optional<Task> todo;
        Set<Task> oldTask;
        synchronized (tasks) {
            oldTask = new HashSet<>(tasks);
            tasks.forEach(t -> t.skip(nanos));
        }
        do {
            synchronized (tasks) {
                todo = tasks.stream().filter(t -> t.getNanos() < 0).findFirst();
            }
            if (todo.isPresent()) {
                final Task todoTask = todo.get();
                todoTask.run();
                synchronized (tasks) {
                    tasks.remove(todoTask);
                    tasks.stream()
                            .filter(t -> !oldTask.contains(t))
                            .forEach(t -> t.skip(todoTask.getNanos() * -1));
                }
            }

        } while (todo.isPresent());
    }

    @Data
    @AllArgsConstructor
    private static class Task implements Comparable<Task> {
        private final Runnable runnable;
        private long nanos;

        void run() {
            this.runnable.run();
        }

        void skip(long deltaNanos) {
            this.nanos -= deltaNanos;
        }

        @Override
        public int compareTo(Task o) {
            return Long.compare(this.getNanos(), o.getNanos());
        }
    }
}
