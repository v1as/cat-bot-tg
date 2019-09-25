package ru.v1as.tg.cat.commands.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Component
@RequiredArgsConstructor
public class TestCommandHandler implements CommandHandler {

    private final UnsafeAbsSender sender;

    @Override
    public String getCommandName() {
        return "test";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        sender.executeUnsafe(
                new SendMessage(chat.getId().toString(), user.getId().toString())
                        .setText("Hello!"));
    }
}
