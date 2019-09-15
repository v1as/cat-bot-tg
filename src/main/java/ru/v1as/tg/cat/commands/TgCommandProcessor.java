package ru.v1as.tg.cat.commands;

import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
public class TgCommandProcessor {

    private Map<String, CommandHandler> commandHandlers = new HashMap<>();

    public TgCommandProcessor register(CommandHandler commandHandler) {
        commandHandlers.put(commandHandler.getCommandName(), commandHandler);
        return this;
    }

    public void process(TgCommandRequest command, Chat chat, User user) {
        CommandHandler commandHandler = commandHandlers.get(command.getName());
        if (commandHandler != null) {
            commandHandler.handle(command, chat, user);
        } else {
            log.warn("No handlers for this command: {}", command);
        }
    }
}
