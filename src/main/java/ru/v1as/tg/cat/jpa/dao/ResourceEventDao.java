package ru.v1as.tg.cat.jpa.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEventEntity;

public interface ResourceEventDao extends JpaRepository<ResourceEventEntity, Long> {

}