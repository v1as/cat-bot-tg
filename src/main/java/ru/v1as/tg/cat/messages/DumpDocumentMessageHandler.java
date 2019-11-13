package ru.v1as.tg.cat.messages;

import java.io.File;
import java.io.IOException;
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
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.UnsafeAbsSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpDocumentMessageHandler extends RequestWithTimeoutCommandHandler {

    private final UnsafeAbsSender sender;
    private final DumpService dumpService;
    private final UserDao userDao;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    protected boolean handleRequest(Message message, Chat chat, User user) {
        final Document document = message.getDocument();
        if (null == document || !document.getFileName().endsWith(".sql")) {
            sender.executeUnsafe(
                    new SendMessage(
                            chat.getId(), "Пришлите файл, который оканчивается на '.sql'."));
            return false;
        }
        InputStream is = getDumpFileInputStream(document);
        final File file = downloadFileFromInputStream(is);
        try {
            dumpService.deleteAllAndLoadDump(file.getAbsolutePath());
        } finally {
            if (file.exists()) {
                if (file.delete()) {
                    log.info("File deleted");
                } else {
                    log.warn("File was not deleted");
                }
            }
        }
        sender.executeUnsafe(
                new SendMessage(
                        chat.getId(), "Файл загружен. Загружено юзеров " + userDao.count()));
        return true;
    }

    private File downloadFileFromInputStream(InputStream is) throws IOException {
        byte[] buffer = new byte[is.available()];
        final int read = is.read(buffer);
        log.debug("Read {}", read);
        final Path path = Paths.get(String.format("cat_database_%s.sql", new Date().getTime()));
        final File file = path.toFile();
        Files.write(path, buffer);
        return file;
    }

    private InputStream getDumpFileInputStream(Document document) throws IOException {
        final GetFile getFile = new GetFile().setFileId(document.getFileId());
        URL url = new URL(Const.getUrlFileDocument(sender.executeUnsafe(getFile).getFilePath()));
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }
}
