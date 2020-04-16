package ru.v1as.tg.cat.commands.impl;

import java.util.List;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class HelpAllCommandHandler extends HelpCommandHandler {

    public HelpAllCommandHandler(@Lazy List<CommandHandler> commands, TgSender sender) {
        super(commands, sender);
    }

    @Override
    public String getCommandName() {
        return "helpa";
    }

    @Override
    public String getCommandDescription() {
        return null; //admins command
    }

    @Override
    protected boolean filterCommand(CommandHandler commandHandler) {
        return true;
    }
}
