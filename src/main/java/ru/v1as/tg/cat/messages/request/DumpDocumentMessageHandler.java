package ru.v1as.tg.cat.messages.request;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
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
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class DumpDocumentMessageHandler extends SimpleRequestCommandHandler {

    private final TgSender sender;
    private final DumpService dumpService;
    private final UserDao userDao;
    private final BotConfiguration conf;

    @Override
    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    protected boolean handleRequest(Message message, TgChat chat, TgUser user) {
        final Document document = message.getDocument();
        if (null == document || !document.getFileName().endsWith(".sql")) {
            sender.execute(
                    new SendMessage(
                            chat.getId(), "Пришлите файл, который оканчивается на '.sql'."));
            return false;
        }
        File file;
        try (InputStream is = getDumpFileInputStream(document)) {
            file = downloadFileFromInputStream(is);
        }
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
        sender.execute(
                new SendMessage(
                        chat.getId(), "Файл загружен. Загружено юзеров " + userDao.count()));
        return true;
    }

    @Override
    public MessageWaitingRequest<Object> addRequest(Message msg) {
        final MessageWaitingRequest<Object> request = super.addRequest(msg);
        request.setTimeout((d) -> sender.message(request.getChatId(), "Не дождался дамп файла."));
        return request;
    }

    private File downloadFileFromInputStream(InputStream is) throws IOException {
        final Path path = Paths.get(String.format("cat_database_%s.sql", new Date().getTime()));
        final File file = path.toFile();
        try (OutputStream outStream = new FileOutputStream(file)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                outStream.write(buffer, 0, bytesRead);
            }
        }
        return file;
    }

    private InputStream getDumpFileInputStream(Document document) throws IOException {
        final GetFile getFile = new GetFile().setFileId(document.getFileId());
        URL url = new URL(conf.getUrlFileDocument(sender.execute(getFile).getFilePath()));
        URLConnection connection = url.openConnection();
        return connection.getInputStream();
    }
}
