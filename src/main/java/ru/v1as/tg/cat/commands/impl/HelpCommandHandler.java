package ru.v1as.tg.cat.commands.impl;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

@Component
@RequiredArgsConstructor
public class HelpCommandHandler implements CommandHandler {

    private final List<CommandHandler> commands;
    private final TgSender sender;

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public void handle(TgCommandRequest command, TgChat chat, TgUser user) {
        final String helpMessage =
                commands.stream()
                        .filter(c -> c.getCommandDescription() != null)
                        .map(
                                c ->
                                        String.format(
                                                "/%s - %s",
                                                c.getCommandName(), c.getCommandDescription()))
                        .collect(Collectors.joining("\n"));
        sender.message(chat, helpMessage);
    }
}
