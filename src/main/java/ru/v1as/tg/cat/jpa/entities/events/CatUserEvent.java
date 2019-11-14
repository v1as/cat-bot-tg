package ru.v1as.tg.cat.jpa.entities.events;

import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT")
@NoArgsConstructor
@ToString(callSuper = true)
public class CatUserEvent extends UserEvent {
    private CatRequestVote result;
    private CatEventType catType;
    private String questName;
    private Integer messageId;

    {
        setDate(LocalDateTime.now());
    }
}
