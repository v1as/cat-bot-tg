package ru.v1as.tg.cat.jpa.dao;

import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;

@Resource
public interface CatUserEventDao extends JpaRepository<CatUserEvent, Long> {

    List<CatUserEvent> findByChatIdAndDateGreaterThan(Long chatId, LocalDateTime after);

}
