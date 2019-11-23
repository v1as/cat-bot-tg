package ru.v1as.tg.cat.jpa.entities.resource;

import java.math.BigDecimal;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEventEntity extends UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne private ResourceEntity resource;
    private BigDecimal delta;
    @ManyToOne private UserEvent event;

    public ResourceEventEntity(
            ResourceEntity resource,
            BigDecimal delta,
            UserEvent event,
            UserEntity user,
            ChatEntity chatEntity) {
        this.resource = resource;
        this.delta = delta;
        this.event = event;
        this.user = user;
        this.chat = chatEntity;
    }
}
