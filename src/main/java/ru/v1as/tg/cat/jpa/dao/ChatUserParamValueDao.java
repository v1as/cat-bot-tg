package ru.v1as.tg.cat.jpa.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParamValue;

public interface ChatUserParamValueDao extends JpaRepository<ChatUserParamValue, Long> {

    Optional<ChatUserParamValue> findByChatIdAndUserIdAndParam(
            Long chatId, Integer userId, ChatUserParam param);
}
