package ru.v1as.tg.cat.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.chat.ChatDetailsEntity;

public interface ChatDetailsDao extends JpaRepository<ChatDetailsEntity, Long> {

    ChatDetailsEntity findByChatId(Long id);
}
