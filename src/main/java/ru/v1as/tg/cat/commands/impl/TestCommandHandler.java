package ru.v1as.tg.cat.commands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.callbacks.phase.PhaseContext;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.JoinCatFollowPhase.Context;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommandHandler implements CommandHandler {

    private final JoinCatFollowPhase testPhase;

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        testPhase.open(testPhase.buildContext(chat));
        log.info("Test phase started...");
    }

}
