package ru.v1as.tg.cat.commands.impl;

import java.time.Duration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.phase.AbstractPhase;
import ru.v1as.tg.cat.callbacks.phase.PollTimeoutConfiguration;
import ru.v1as.tg.cat.callbacks.phase.poll.ChooseContext;
import ru.v1as.tg.cat.callbacks.phase.poll.PollChoice;
import ru.v1as.tg.cat.commands.ArgumentCallbackCommand.CallbackCommandContext;
import ru.v1as.tg.cat.model.UserData;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestPhase extends AbstractPhase {

    private final CatBotData botData;
    private final StartCommand startCommand;

    @Override
    public void open() {
        sendMessage("Привет");
        PollChoice joinPullChoice = PollChoice.startCommandUrl("Выбор 2!");
        poll("Тест")
                .choice("Выбор 1", this::choose1)
                .choice(joinPullChoice)
                .onSend(m -> log.info("Message!!!! " + m.toString()))
                .removeOnChoice(true)
                .timeout(
                        new PollTimeoutConfiguration(Duration.ofSeconds(3))
                                .removeMsg(true)
                                .onTimeout(this::timeIsOver))
                .send();
//        startCommand.register(joinPullChoice.getUuid(), contextWrap(this::choose2));
        onClose(() -> startCommand.drop(joinPullChoice.getUuid()));
    }

    private void timeIsOver() {
        sendMessage("Похоже вы не успели....");
    }

    private void choose2(CallbackCommandContext data) {
        startCommand.drop(data.getArgument());
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
