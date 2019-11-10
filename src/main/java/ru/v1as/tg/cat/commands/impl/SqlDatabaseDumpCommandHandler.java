package ru.v1as.tg.cat.commands.impl;

import static ru.v1as.tg.cat.Const.onlyForAdminCheck;

import java.io.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.commands.CommandHandler;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Component
@RequiredArgsConstructor
public class SqlDatabaseDumpCommandHandler implements CommandHandler {

    private final DumpService dumpService;
    private final UnsafeAbsSender sender;

    @Override
    public String getCommandName() {
        return "database_dump";
    }

    @Override
    public void handle(TgCommandRequest command, Chat chat, User user) {
        onlyForAdminCheck(user);
        final String fileName = dumpService.write();
        final File dumpFile = new File(fileName);
        if (dumpFile.exists()) {
            SendDocument document = new SendDocument();
            document.setChatId(chat.getId());
            document.setDocument(dumpFile);
            sender.executeUnsafe(document);
            if (!dumpFile.delete()) {
                sender.executeUnsafe(new SendMessage(chat.getId(), "Файл не удалось удалить"));
            }
        } else {
            sender.executeUnsafe(new SendMessage(chat.getId(), "Ошибка создания файла"));
        }
    }
}
