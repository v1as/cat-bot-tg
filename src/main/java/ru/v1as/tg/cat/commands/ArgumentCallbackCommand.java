package ru.v1as.tg.cat.commands;

import static org.springframework.util.StringUtils.isEmpty;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import lombok.Value;
import org.slf4j.Logger;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

public abstract class ArgumentCallbackCommand implements CommandHandler {

    private final Logger log = org.slf4j.LoggerFactory.getLogger(this.getClass());
    private final Consumer<CallbackCommandContext> defaultConsumer;
    private Map<String, Consumer<CallbackCommandContext>> callbacks = new ConcurrentHashMap<>();

    public ArgumentCallbackCommand(Consumer<CallbackCommandContext> defaultConsumer) {
        this.defaultConsumer = defaultConsumer;
    }

    public ArgumentCallbackCommand() {
        this(null);
    }

    @Override
    public void handle(TgCommandRequest command, TgChat chat, TgUser user) {
        String argument = command.getFirstArgument();
        if (isEmpty(argument) || !callbacks.containsKey(argument)) {
            if (defaultConsumer != null) {
                defaultConsumer.accept(new CallbackCommandContext(command, chat, user, null));
            } else {
                log.warn("No callback registered for this command ctx {}", command);
            }
        } else {
            Consumer<CallbackCommandContext> consumer = callbacks.get(argument);
            consumer.accept(new CallbackCommandContext(command, chat, user, argument));
        }
    }

    public void register(String argument, Consumer<CallbackCommandContext> callback) {
        if (callbacks.containsKey(argument)) {
            throw new IllegalArgumentException("Such argument is already registered");
        }
        this.callbacks.put(argument, callback);
        log.debug(
                "Callback command '{}' is registered for command '{}'", argument, getCommandName());
    }

    public void drop(String argument) {
        if (!callbacks.containsKey(argument)) {
            log.warn(
                    "Callback command '{}' is not registered for command '{}'",
                    argument,
                    getCommandName());
        } else {
            callbacks.remove(argument);
        }
    }

    @Value
    public static class CallbackCommandContext {
        TgCommandRequest command;
        TgChat chat;
        TgUser user;
        String argument;
    }
}
