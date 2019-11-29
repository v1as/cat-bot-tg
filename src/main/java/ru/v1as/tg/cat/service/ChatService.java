package ru.v1as.tg.cat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMembersCount;
import ru.v1as.tg.cat.jpa.dao.ChatDetailsDao;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;
import ru.v1as.tg.cat.tg.TgSender;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatDetailsDao chatDetailsDao;
    private final TgSender tgSender;

    @Async
    @Transactional
    public void updateChatDetailsAmount(Long chatId) {
        final ChatDetailsEntity chatDetails = chatDetailsDao.findByChatId(chatId);
        final Integer amounts = tgSender.execute(new GetChatMembersCount().setChatId(chatId));
        chatDetails.setMembersAmount(amounts);
        chatDetailsDao.save(chatDetails);
    }
}
