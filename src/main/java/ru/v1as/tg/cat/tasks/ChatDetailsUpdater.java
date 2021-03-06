package ru.v1as.tg.cat.tasks;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import ru.v1as.tg.cat.jpa.dao.ChatDao;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatDetailsUpdater {
    private final ChatDetailsDao chatDetailsDao;
    private final ChatDao chatDao;
    private final TgSender sender;

    @Scheduled(cron = "0 55 9 * * *")
    public void run() {
        update();
    }

    @SneakyThrows
    public int update() {
        int liveChats = 0;
        for (ChatEntity chat : chatDao.findAll()) {
            ChatDetailsEntity details =
                    chatDetailsDao
                            .findById(chat.getId())
                            .orElse(new ChatDetailsEntity(chat.getId(), chat, 0, false));
            try {
                final Integer amount =
                        sender.execute(new GetChatMembersCount().setChatId(chat.getId()));
                details.setMembersAmount(amount);
                details.getChat().setUpdated(LocalDateTime.now());
                details.setEnabled(true);
                chatDetailsDao.save(details);
                chatDao.save(details.getChat());
                liveChats++;
            } catch (Exception e) {
                details.setEnabled(false);
                chatDetailsDao.save(details);
                log.debug("Error while details amount updating: ", e);
            }
            Thread.sleep(500);
        }
        log.info("Live chats '{}' updated", liveChats);
        return liveChats;
    }
}
