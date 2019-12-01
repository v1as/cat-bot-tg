package ru.v1as.tg.cat.service;

import static ru.v1as.tg.cat.service.Const.getAdminUserNames;

import java.util.List;
import java.util.Optional;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.v1as.tg.cat.jpa.dao.UserDao;
import ru.v1as.tg.cat.jpa.dao.UserEventDao;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.tg.TgSender;

@Service
@RequiredArgsConstructor
public class EventAdminSender {

    private final UserDao userDao;
    private final UserEventDao userEventDao;
    private final TgSender tgSender;
    private Long lastId;

    @PostConstruct
    public void init() {
        this.lastId = userEventDao.findTopByOrderByIdDesc().map(UserEvent::getId).orElse(-1L);
    }

    @Scheduled(fixedRate = 30000)
    public void sendEvents() {
        final List<UserEvent> events = userEventDao.findAllByIdGreaterThan(lastId);
        if (!events.isEmpty()) {
            lastId = events.get(events.size() - 1).getId();
            for (String adminUserName : getAdminUserNames()) {
                final Optional<UserEntity> admin = userDao.findByUserName(adminUserName);
                if (admin.isPresent() && admin.get().isPrivateChat()) {
                    final long chatId = admin.get().getId();
                    if (events.size() > 20) {
                        tgSender.execute(new SendMessage(chatId, "Слишком много сообщений"));
                    } else {
                        tgSender.execute(new SendMessage(chatId, events.toString()));
                    }
                }
            }
        }
    }
}
