package ru.v1as.tg.cat.commands.impl;

import static java.lang.Long.parseLong;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;

@Slf4j
@Component
public class SendCommand extends AbstractCommand {

    private final ChatDao chatDao;

    public SendCommand(ChatDao chatDao) {
        super(cfg().commandName("send").onlyBotAdmins(true).onlyPrivateChat(true));
        this.chatDao = chatDao;
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        if (command.getArguments().isEmpty()) {
            return;
        }
        try {
            final long chatId = parseLong(command.getFirstArgument());

        } catch (NumberFormatException e) {
            // nothing to do
        }
    }
}
