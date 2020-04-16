package ru.v1as.tg.cat.jpa.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.chat.ChatParamValue;
import ru.v1as.tg.cat.service.ChatParam;

public interface ChatParamValueDao extends JpaRepository<ChatParamValue, Long> {

    Optional<ChatParamValue> findByChatIdAndParam(Long chatId, ChatParam param);
}
