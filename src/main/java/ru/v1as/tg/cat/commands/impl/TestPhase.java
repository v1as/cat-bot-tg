package ru.v1as.tg.cat.commands.impl;

import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.model.ChatData;
import ru.v1as.tg.cat.model.UserData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
public class TestPhase extends AbstractPhase {

    private final CatBotData botData;

    public TestPhase(
            UnsafeAbsSender sender,
            TgCallbackProcessor callbackProcessor,
            ChatData chat,
            CatBotData botData) {
        super(sender, callbackProcessor, chat);
        this.botData = botData;
    }

    @Override
    public void open() {
        sendMessage("Привет");
        poll("Тест")
                .choice("Выбор 1", this::choose1)
                .choice("Выбор 2", this::choose2)
                .onSend(m -> log.info("Message!!!! " + m.toString()))
                .removeOnChoice(true)
                .timeout(
                        new PollTimeoutConfiguration(Duration.ofSeconds(3))
                                .removeMsg(true)
                                .onTimeout(this::timeout))
                .send();
    }

    private void timeout() {
        sendMessage("Похоже вы не успели....");
    }

    private void choose2(ChooseContext data) {
        UserData user = botData.getUserData(data.getUser());
        sendMessage("Вы выбрали вариант 2, " + user.getUsernameOrFullName());
        close();
    }

    private void choose1(ChooseContext data) {
        UserData user = botData.getUserData(data.getUser());
        sendMessage("Вы выбрали вариант 1, " + user.getUsernameOrFullName());
        close();
    }
}
