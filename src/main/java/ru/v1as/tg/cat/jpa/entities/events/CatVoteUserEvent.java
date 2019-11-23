package ru.v1as.tg.cat.jpa.entities.events;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT_VOTE")
@NoArgsConstructor
@ToString(callSuper = true)
public class CatVoteUserEvent extends UserEvent {
    private Integer messageId;
    @Enumerated private CatRequestVote vote;
}
