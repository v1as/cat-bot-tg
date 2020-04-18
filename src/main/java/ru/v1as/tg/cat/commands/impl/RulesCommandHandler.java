package ru.v1as.tg.cat.commands.impl;

import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.AbstractCommand.Configuration.ConfigurationBuilder;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.ResourceTexts;

@Component
public class RulesCommandHandler extends AbstractCommand {

    private final String rulesText;

    public RulesCommandHandler(ResourceTexts texts) {
        super(new ConfigurationBuilder().commandName("rules"));
        this.rulesText = texts.load("rules");
    }

    @Override
    @SneakyThrows
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        sender.message(chat, rulesText);
    }

    @Override
    public String getCommandDescription() {
        return "Правила игры";
    }
}
