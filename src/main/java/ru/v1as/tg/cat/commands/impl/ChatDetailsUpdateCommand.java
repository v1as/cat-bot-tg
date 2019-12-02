package ru.v1as.tg.cat.commands.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tasks.ChatDetailsUpdater;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class ChatDetailsUpdateCommand extends AbstractCommand {

    @Autowired private ChatDetailsUpdater chatDetailsUpdater;
    @Autowired private TgSender sender;

    public ChatDetailsUpdateCommand() {
        super(cfg().onlyPrivateChat(true).onlyBotAdmins(true).commandName("update_chats"));
    }

    @Override
    protected void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final int chats = chatDetailsUpdater.update();
        sender.message(chat, "Чатов обновлено " + chats);
    }
}
