package ru.v1as.tg.cat.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;

public interface UserEventDao extends JpaRepository<UserEvent, Long> {}
