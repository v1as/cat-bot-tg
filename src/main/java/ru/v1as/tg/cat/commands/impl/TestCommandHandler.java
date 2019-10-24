package ru.v1as.tg.cat.commands.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.CatBotData;
import ru.v1as.tg.cat.callbacks.TgCallbackProcessor;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.CatChatData;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestCommandHandler implements CommandHandler {

    private final UnsafeAbsSender sender;
    private final TgCallbackProcessor callbackProcessor;
    private final CatBotData data;

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        CatChatData chatData = data.getChatData(chat.getId());
        new TestPhase(sender, callbackProcessor, chatData, data).open();
        log.info("Test phase started...");
    }
}
