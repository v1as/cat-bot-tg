package ru.v1as.tg.cat.jpa.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;

public interface ChatDao extends JpaRepository<ChatEntity, Long> {
    List<ChatEntity> findByUsersId(Integer id);
}
