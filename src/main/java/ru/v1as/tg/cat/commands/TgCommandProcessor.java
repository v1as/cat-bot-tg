package ru.v1as.tg.cat.commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public interface TgCommandProcessor {

    void register(CommandHandler commandHandler);

    void process(TgCommandRequest command, Chat chat, User user);

    void drop(String commandName);
}
