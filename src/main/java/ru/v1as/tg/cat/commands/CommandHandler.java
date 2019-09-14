package ru.v1as.tg.cat.commands;

import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public interface CommandHandler {

    String getCommandName();

    void handle(TgCommandRequest command, Chat chat, User user);

}
