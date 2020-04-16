package ru.v1as.tg.cat.tasks;

import static ru.v1as.tg.cat.service.ChatParam.CAT_BITE_LEVEL;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.service.ChatParamResource;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class CuriosCatRequestScheduler {

    private final JoinCatFollowPhase joinCatFollowPhase;
    private final ChatDao chatDao;
    private final Random random = new Random();
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private boolean firstTime = true;
    private int delayRange = 60;
    private int delayMin = 30;
    private double chance = 0.2;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private final ChatParamResource paramResource;

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

        final List<ChatEntity> chats = chatDao.findAll();
        int chatSent = 0;
        for (ChatEntity chat : chats) {
            try {
                double randomResult = random.nextDouble();
                final int catBite = paramResource.paramInt(chat, CAT_BITE_LEVEL);
                if (randomResult < chance + increaseChance(catBite)) {
                    log.info("Curios cat is sending to chat {}", chat);
                    joinCatFollowPhase.open(chat);
                }
                chatSent++;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Curios cat was executed for {} chats", chatSent);
    }

    double increaseChance(int catBite) {
        return catBite * (1 - chance) / (double) CAT_BITE_LEVEL.getMaxValue();
    }
}
