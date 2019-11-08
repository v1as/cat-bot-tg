package ru.v1as.tg.cat.commands;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

@Slf4j
@Component
public class TgCommandProcessorByName implements TgCommandProcessor {

    private final Map<String, CommandHandler> commandToHandler = new HashMap<>();

    public TgCommandProcessorByName(List<CommandHandler> handlers) {
        handlers.forEach(this::register);
    }

    @Override
    public void register(CommandHandler commandHandler) {
        if (commandToHandler.containsKey(commandHandler.getCommandName())) {
            throw new IllegalStateException(
                    "Command with such name is already registered" + commandHandler.getClass());
        }
        commandToHandler.put(commandHandler.getCommandName(), commandHandler);
        log.info(
                "Command '{}' with  name '{}' registered.",
                commandHandler.getClass().getSimpleName(),
                commandHandler.getCommandName());
    }

    @Override
    public void process(TgCommandRequest command, Chat chat, User user) {
        CommandHandler commandHandler = commandToHandler.get(command.getName());
        if (commandHandler != null) {
            log.info(
                    "Command '{}' just come from user '{}' in chat '{}'.  It will be processed by handler: '{}'",
                    commandHandler.getCommandName(),
                    user,
                    chat,
                    commandHandler.getClass().getName());
            commandHandler.handle(command, chat, user);
        } else {
            log.info("No handlers for this command: {}", command);
        }
    }

    @Override
    public void drop(String commandName) {
        commandToHandler.remove(commandName);
    }
}
