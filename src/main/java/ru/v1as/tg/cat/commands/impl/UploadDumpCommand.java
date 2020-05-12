package ru.v1as.tg.cat.commands.impl;

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
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.commands.TgCommandRequest;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.messages.request.MessageRequest;
import ru.v1as.tg.cat.messages.request.RequestMessageHandler;
import ru.v1as.tg.cat.model.TgChat;
import ru.v1as.tg.cat.model.TgUser;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
public class UploadDumpCommand extends AbstractCommand {

    private final DumpService dumpService;
    private final UserDao userDao;
    private final BotConfiguration conf;
    private final RequestMessageHandler requestMessageHandler;
    private final TgSender sender;
    private final UploadDumpCommand self;

    public UploadDumpCommand(
            RequestMessageHandler requestMessageHandler,
            TgSender sender,
            DumpService dumpService,
            UserDao userDao,
            BotConfiguration conf,
            @Lazy UploadDumpCommand self) {
        super(cfg().onlyBotAdmins(true).onlyPrivateChat(true).commandName("upload_dump"));
        this.requestMessageHandler = requestMessageHandler;
        this.sender = sender;
        this.dumpService = dumpService;
        this.userDao = userDao;
        this.conf = conf;
        this.self = self;
    }

    @Override
    public void process(TgCommandRequest command, TgChat chat, TgUser user) {
        sender.message(chat, "Теперь пришлите dump.sql файл");
        requestMessageHandler.addRequest(
                new MessageRequest(command.getMessage())
                        .filter(
                                m -> {
                                    final Document document = m.getDocument();
                                    return null != document
                                            && document.getFileName().endsWith(".sql");
                                })
                        .onResponse(m -> self.onResponse(m, chat, user))
                        .onTimeout(() -> sender.message(chat, "Не дождался дамп файла.")));
    }

    @SneakyThrows
    @Transactional(rollbackFor = Exception.class)
    protected void onResponse(Message message, TgChat chat, TgUser user) {
        final Document document = message.getDocument();
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
        sender.message(chat, "Файл загружен. Загружено юзеров " + userDao.count());
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
