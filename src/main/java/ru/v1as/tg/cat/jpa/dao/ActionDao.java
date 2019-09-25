package ru.v1as.tg.cat.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.v1as.tg.cat.jpa.entities.Action;

@Repository
public interface ActionDao extends JpaRepository<Action, Long> {

}
