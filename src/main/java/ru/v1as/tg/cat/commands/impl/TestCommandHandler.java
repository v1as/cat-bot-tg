package ru.v1as.tg.cat.commands.impl;

import static ru.v1as.tg.cat.service.Const.onlyForAdminCheck;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import ru.v1as.tg.cat.callbacks.phase.impl.JoinCatFollowPhase;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommandHandler implements CommandHandler {

    private final JoinCatFollowPhase testPhase;
    private final TgSender sender;

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public void handle(TgCommandRequest command, TgChat chat, TgUser user) {
        if (chat.isUserChat()) {
            sender.message(chat, "Эту комманду можно выполнять только в групповом чате.");
            return;
        }
        onlyForAdminCheck(user);

        testPhase.open(chat);
        sender.execute(new DeleteMessage(chat.getId(), command.getMessageId()));
        log.info("Test phase started...");
    }
}
