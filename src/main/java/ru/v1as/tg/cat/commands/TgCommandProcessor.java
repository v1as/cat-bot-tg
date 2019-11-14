package ru.v1as.tg.cat.commands;

import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public interface TgCommandProcessor {

    void register(CommandHandler commandHandler);

    void process(TgCommandRequest command, TgChat chat, TgUser user);

    void drop(String commandName);
}
