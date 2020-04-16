package ru.v1as.tg.cat.jpa.dao;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.resource.ResourceEvent;

public interface ResourceEventDao extends JpaRepository<ResourceEvent, Long> {

    List<ResourceEvent> findByResourceId(Long id);
}
