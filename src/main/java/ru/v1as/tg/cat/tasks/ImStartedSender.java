package ru.v1as.tg.cat.tasks;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.service.BotConfiguration;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class ImStartedSender {

    private final TgSender sender;
    private final BotConfiguration conf;
    private final UserDao userDao;

    @PostConstruct
    public void init() {
        final List<UserEntity> admins =
                conf.getAdminUserNames().stream()
                        .map(userDao::findByUserName)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
        for (UserEntity userEntity : admins) {
            try {
                sender.execute(new SendMessage((long) userEntity.getId(), "Погнали!"));
            } catch (Exception e) {
                log.error("Error message sending ", e);
            }
        }
    }
}
