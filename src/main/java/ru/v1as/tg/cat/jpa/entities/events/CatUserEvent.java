package ru.v1as.tg.cat.jpa.entities.events;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT")
public class CatUserEvent extends UserEvent {
    private CatRequestVote result;
    private boolean isCurios;
    private String questName;
}
