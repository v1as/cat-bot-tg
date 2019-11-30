package ru.v1as.tg.cat.jpa.entities.events;

import static javax.persistence.EnumType.STRING;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT_VOTE")
@ToString(callSuper = true)
@NoArgsConstructor
public class CatVoteEvent extends UserEvent {
    @ManyToOne(optional = false)
    private CatUserEvent event;

    @Column(nullable = false)
    @Enumerated(STRING)
    private CatRequestVote vote;

    public CatVoteEvent(ChatEntity chat, UserEntity user, CatUserEvent event, CatRequestVote vote) {
        this.chat = chat;
        this.user = user;
        this.event = event;
        this.vote = vote;
        this.date = LocalDateTime.now();
    }
}
