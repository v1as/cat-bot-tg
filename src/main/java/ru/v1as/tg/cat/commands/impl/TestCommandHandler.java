package ru.v1as.tg.cat.commands.impl;

import static ru.v1as.tg.cat.Const.onlyForAdminCheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;

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
        onlyForAdminCheck(user);
        testPhase.open(chat);
        log.info("Test phase started...");
    }
}
