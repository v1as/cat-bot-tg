package ru.v1as.tg.cat.commands.impl;

import java.io.File;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.init.ZipDumpService;
import ru.v1as.tg.cat.tg.TgSender;

@Component
public class SqlDatabaseDumpCommandHandler extends AbstractCommand {

    private final ZipDumpService dumpService;
    private final TgSender sender;

    public SqlDatabaseDumpCommandHandler(ZipDumpService dumpService, TgSender sender) {
        super(cfg().onlyPrivateChat(true).onlyBotAdmins(true).commandName("database_dump"));
        this.dumpService = dumpService;
        this.sender = sender;
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        final String fileName = dumpService.write();
        final File dumpFile = new File(fileName);
        if (dumpFile.exists()) {
            SendDocument document = new SendDocument();
            document.setChatId(chat.getId());
            document.setDocument(dumpFile);
            sender.executeDoc(document);
            if (!dumpFile.delete()) {
                sender.execute(new SendMessage(chat.getId(), "Файл не удалось удалить"));
            }
        } else {
            sender.execute(new SendMessage(chat.getId(), "Ошибка создания файла"));
        }
    }
}
