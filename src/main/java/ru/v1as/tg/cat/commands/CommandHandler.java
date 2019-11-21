package ru.v1as.tg.cat.commands;

import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface CommandHandler {

    String getCommandName();

    void handle(TgCommandRequest command, TgChat chat, TgUser user);

    default String getCommandDescription() {
        return null;
    }
}
