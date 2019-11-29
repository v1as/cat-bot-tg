package ru.v1as.tg.cat.jpa.entities.events;

import static javax.persistence.EnumType.STRING;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT_VOTE")
@ToString(callSuper = true)
public class CatVoteEvent extends UserEvent {
    private Integer messageId;

    @Enumerated(STRING)
    private CatRequestVote vote;
}
