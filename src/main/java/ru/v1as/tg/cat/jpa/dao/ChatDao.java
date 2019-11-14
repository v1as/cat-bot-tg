package ru.v1as.tg.cat.jpa.dao;

import javax.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;

@Resource
public interface ChatDao extends JpaRepository<ChatEntity, Long> {}
