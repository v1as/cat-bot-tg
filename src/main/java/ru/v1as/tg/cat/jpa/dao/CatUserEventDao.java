package ru.v1as.tg.cat.jpa.dao;

import javax.annotation.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.events.CatUserEvent;

@Resource
public interface CatUserEventDao extends JpaRepository<CatUserEvent, Long> {}
