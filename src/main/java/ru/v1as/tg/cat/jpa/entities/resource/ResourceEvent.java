package ru.v1as.tg.cat.jpa.entities.resource;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.events.UserEvent;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Data
@Entity
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ResourceEvent extends UserEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    private ResourceEntity resource;

    private BigDecimal delta;

    @ManyToOne(optional = false)
    private UserEvent event;

    public ResourceEvent(
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
        this.date = LocalDateTime.now();
    }
}
