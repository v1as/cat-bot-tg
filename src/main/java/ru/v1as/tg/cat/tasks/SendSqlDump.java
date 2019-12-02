package ru.v1as.tg.cat.tasks;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.service.init.DumpService;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class SendSqlDump {
    private final TgSender sender;
    private final UserDao userDao;
    private final BotConfiguration conf;
    private final DumpService dumpService;

    @PostConstruct
    public void init() {
        log.info("SendSqlDump sender was inited");
    }

    @Scheduled(cron = "0 50 9 * * *")
    public void run() {
        final List<UserEntity> admins =
                conf.getAdminUserNames().stream()
                        .map(userDao::findByUserName)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .filter(UserEntity::isPrivateChat)
                        .collect(Collectors.toList());
        for (UserEntity admin : admins) {
            final String fileName = dumpService.write();
            final File dumpFile = new File(fileName);
            final long chatId = admin.getId();
            if (dumpFile.exists()) {
                SendDocument document = new SendDocument();
                document.setChatId(chatId);
                document.setDocument(dumpFile);
                sender.executeDoc(document);
                if (!dumpFile.delete()) {
                    sender.execute(new SendMessage(chatId, "Файл не удалось удалить"));
                }
            } else {
                sender.execute(new SendMessage(chatId, "Ошибка создания файла"));
            }
        }
    }
}
