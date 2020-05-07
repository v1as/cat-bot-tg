package ru.v1as.tg.cat.commands.impl;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.messages.request.DumpDocumentMessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class UploadDumpCommand extends AbstractCommand {

    private final DumpDocumentMessageHandler dumpDocumentMessageHandler;
    private final TgSender sender;

    public UploadDumpCommand(
            DumpDocumentMessageHandler dumpDocumentMessageHandler, TgSender sender) {
        super(cfg().onlyBotAdmins(true).onlyPrivateChat(true).commandName("upload_dump"));
        this.dumpDocumentMessageHandler = dumpDocumentMessageHandler;
        this.sender = sender;
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        dumpDocumentMessageHandler.addRequest(command.getMessage());
        sender.execute(new SendMessage(chat.getId(), "Теперь пришлите dump.sql файл"));
    }
}
