package ru.v1as.tg.cat.commands;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;

@Slf4j
@Component
public class TgCommandProcessorByName implements TgCommandProcessor {

    private final Map<String, CommandHandler> commandToHandler = new HashMap<>();
    private final BotConfiguration conf;

    public TgCommandProcessorByName(List<CommandHandler> handlers, BotConfiguration conf) {
        handlers.forEach(this::register);
        this.conf = conf;
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
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        if (!isEmpty(command.getBotName()) && !command.getBotName().equals(conf.getBotName())) {
            log.debug("Skipping command {} because of other bot name.", command);
            return;
        }
        CommandHandler commandHandler = commandToHandler.get(command.getName());
        if (commandHandler != null) {
            log.info(
                    "Command '{}' will be processed by handler: '{}'",
                    commandHandler.getCommandName(),
                    commandHandler.getClass().getName());
            commandHandler.handle(command, chat, user);
        } else {
            log.debug("No handlers for this command: {}", command);
        }
    }

    @Override
    public void drop(String commandName) {
        commandToHandler.remove(commandName);
    }
}
