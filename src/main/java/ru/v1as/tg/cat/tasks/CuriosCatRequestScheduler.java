package ru.v1as.tg.cat.tasks;

import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.commands.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.model.CatChatData;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class CuriosCatRequestScheduler {

    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);

    private final CatBotData data;
    private final JoinCatFollowPhase joinCatFollowPhase;

    private final Random random = new Random();
    private boolean firstTime = true;
    private int delayRange = 60;
    private int delayMin = 30;
    private double chance = 0.2;
    private TimeUnit timeUnit = TimeUnit.MINUTES;

    @PostConstruct
    public void init() {
        run();
    }

    void run() {
        int minutes = random.nextInt(delayRange) + delayMin;
        executorService.schedule(this::run, minutes, timeUnit);
        log.info("Next curios cat scheduled in {} {}", minutes, timeUnit);
        if (firstTime) {
            firstTime = false;
            return;
        }
        for (CatChatData chat : data.getChats()) {
            if (chat.isPrivate()) {
                continue;
            }
            try {
                double randomResult = random.nextDouble();
                log.info(
                        "Random result for curios cat {} in chat '{}'.",
                        randomResult,
                        chat.getName());
                if (randomResult < chance) {
                    log.info("Curios cat is sending...");
                    joinCatFollowPhase.open(chat.getChat());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Curios cat was executed for {} chats", data.getChats().size());
    }
}
