package ru.v1as.tg.cat.messages;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.v1as.tg.cat.Const;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoadDumpMessageHandler implements MessageHandler {

    private final UnsafeAbsSender sender;
    private final DumpService dumpService;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    public void handle(Message message, Chat chat, User user) {
        if (!message.hasDocument()) {
            return;
        }
        //        if (!id.equals(user.getId()) || !chatId.equals(chat.getId()) ||
        // !message.hasDocument()) {
        //            return;
        //        }
        final Document document = message.getDocument();
        if (document.getFileName().endsWith(".sql")) {
            final GetFile getFile = new GetFile().setFileId(document.getFileId());
            URL url =
                    new URL(Const.getUrlFileDocument(sender.executeUnsafe(getFile).getFilePath()));
            URLConnection connection = url.openConnection();
            InputStream is = connection.getInputStream();
            byte[] buffer = new byte[is.available()];
            final int read = is.read(buffer);
            log.debug("Readed {}", read);
            final Path path = Paths.get(String.format("cat_database_%s.sql", new Date().getTime()));
            final File file = path.toFile();
            try {
                Files.write(path, buffer);
                dumpService.deleteAllAndLoad(file.getAbsolutePath());
            } finally {
                if (file.exists()) {
                    if (file.delete()) {
                        log.info("File deleted");
                    } else {
                        log.warn("File was not deleted");
                    }
                }
            }
            sender.executeUnsafe(new SendMessage(chat.getId(), "Файл загружен."));
        }
    }
}
