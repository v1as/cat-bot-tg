package ru.v1as.tg.cat.service.clock;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("test")
public class TestBotClock implements BotClock {

    private final Set<Task> tasks = new TreeSet<>();
    private AtomicLong ids = new AtomicLong();

    @Override
    public void wait(int milliseconds) {
        // nothing to do here
    }

    @Override
    public void schedule(Runnable runnable, long delay, TimeUnit unit) {
        final long id = ids.incrementAndGet();
        synchronized (tasks) {
            final Task task = new Task(id, runnable, unit.toNanos(delay));
            tasks.add(task);
            log.debug("Scheduled task {} in {} {}", task, delay, unit);
        }
        log.debug("Actual tasks: {}", tasks);
    }

    public void skip(long delay, TimeUnit timeUnit) {
        log.debug("Skip time {} {}", delay, timeUnit);
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
                log.debug("Running task {}", todoTask);
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

    public void reset() {
        tasks.clear();
    }

    @Data
    @EqualsAndHashCode(of = "id")
    @AllArgsConstructor
    private static class Task implements Comparable<Task> {
        private static final Comparator<Task> COMPARATOR =
                Comparator.comparing(Task::getNanos).thenComparing(Task::getId);

        private final long id;
        private final Runnable runnable;
        private long nanos;

        void run() {
            this.runnable.run();
        }

        void skip(long deltaNanos) {
            this.nanos -= deltaNanos;
        }

        @Override
        public int compareTo(Task that) {
            return COMPARATOR.compare(this, that);
        }

        @Override
        public String toString() {
            return String.format("[%s:%s]", id, runnable);
        }
    }
}
