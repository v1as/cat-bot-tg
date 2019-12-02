package ru.v1as.tg.cat.commands.impl;

import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.AbstractCommand.Configuration.ConfigurationBuilder;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.tg.TgSender;

public abstract class AbstractCommand implements CommandHandler {

    private final Configuration configuration;
    @Autowired protected BotConfiguration conf;
    @Autowired protected TgSender sender;

    public AbstractCommand(ConfigurationBuilder configuration) {
        this.configuration = configuration.build();
    }

    protected static ConfigurationBuilder cfg() {
        return Configuration.builder();
    }

    @Override
    public String getCommandName() {
        return configuration.commandName;
    }

    @Override
    public final void handle(TgCommandRequest command, TgChat chat, TgUser user) {
        if (configuration.onlyAdmin && !conf.isBotAdmin(user)) {
            sender.message(chat, "Только администратор бота может выполнять эту комманду.");
            return;
        }
        if (configuration.onlyPrivateChat) {
            if (!chat.isUserChat()) {
                sender.message(chat, "Эта команда разрешена только в приватном чате");
                return;
            }
        }
        if (configuration.onlyPublicChat) {
            if (chat.isUserChat()) {
                sender.message(chat, "Эта команда разрешена только в публичном чате.");
                return;
            }
        }
        process(command, chat, user);
    }

    protected abstract void process(TgCommandRequest command, TgChat chat, TgUser user);

    @Builder
    static class Configuration {
        private boolean onlyBotAdmins;
        private boolean onlyPrivateChat;
        private boolean onlyPublicChat;
        private boolean onlyAdmin;
        private String commandName;
    }
}
