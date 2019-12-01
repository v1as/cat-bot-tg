package ru.v1as.tg.cat.jpa.dao;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;

public interface UserEventDao extends JpaRepository<UserEvent, Long> {

    Optional<UserEvent> findTopByOrderByIdDesc();

    List<UserEvent> findAllByIdGreaterThan(Long lastId);
}
