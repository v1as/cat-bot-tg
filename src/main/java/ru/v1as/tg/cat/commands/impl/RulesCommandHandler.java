package ru.v1as.tg.cat.commands.impl;

import java.nio.file.Files;
import java.nio.file.Paths;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.commands.impl.AbstractCommand.Configuration.ConfigurationBuilder;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Component
public class RulesCommandHandler extends AbstractCommand {

    @Value("classpath:text/rules.txt")
    private Resource rules;

    public RulesCommandHandler() {
        super(new ConfigurationBuilder().commandName("rules"));
    }

    @Override
    @SneakyThrows
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final byte[] bytes = Files.readAllBytes(Paths.get(rules.getURI()));
        sender.message(chat, new String(bytes));
    }

    @Override
    public String getCommandDescription() {
        return "Правила игры";
    }

}
