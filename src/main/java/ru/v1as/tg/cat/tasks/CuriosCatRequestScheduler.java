package ru.v1as.tg.cat.tasks;

import static ru.v1as.tg.cat.tg.KeyboardUtils.deleteMsg;
import static ru.v1as.tg.cat.tg.KeyboardUtils.inlineKeyboardMarkup;

import java.util.ArrayList;
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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.EmojiConst;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.curios.CuriosCatPolLCallback;
import ru.v1as.tg.cat.callbacks.phase.CuriosCatPhase;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.model.CuriosCatRequest;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class CuriosCatRequestScheduler {

    private final TgCallbackProcessor callbackProcessor;
    private final CatBotData data;
    private final UnsafeAbsSender sender;

    private final Random random = new Random();
    private ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
    private boolean firstTime = true;
    private int delayRange = 60;
    private int delayMin = 30;
    private double chance = 0.2;
    private int closeDelay = 5;
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
        List<CuriosCatRequest> requests = new ArrayList<>();
        for (CatChatData chat : data.getChats()) {
            try {
                double randomResult = random.nextDouble();
                log.info(
                        "Random result for curios cat {} in chat '{}'.",
                        randomResult,
                        chat.getName());
                if (randomResult < chance) {
                    log.info("Curios cat is sending...");
                    requests.add(sendMessage(chat));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        log.info("Curios cat was executed for {} chats", data.getChats().size());
        executorService.schedule(() -> closeOpenedRequests(requests), closeDelay, timeUnit);
    }

    void closeOpenedRequests(List<CuriosCatRequest> requests) {
        requests.stream()
                .filter(CuriosCatRequest::isOpen)
                .forEach(
                        r -> {
                            r.cancel();
                            sender.executeUnsafe(deleteMsg(r.getVoteMessage()));
                        });
    }

    CuriosCatRequest sendMessage(CatChatData chat) {
        CuriosCatRequest request = new CuriosCatRequest(chat);

        new CuriosCatPhase(sender, callbackProcessor, chat, data.getScoreData()).open();
        sender.executeAsyncUnsafe(
                new SendMessage()
                        .setChatId(chat.getChatId())
                        .disableNotification()
                        .setText(getCuriosCatMessage())
                        .setReplyMarkup(
                                inlineKeyboardMarkup(EmojiConst.CAT + " Кот!", "curiosCat")),
                new CuriosCatPolLCallback(chat, request));
        return request;
    }

    private String getCuriosCatMessage() {
        return "Любопытный кот гуляет рядом";
    }

    void setExecutorService(ScheduledExecutorService executorService) {
        this.executorService = executorService;
    }
}
