package ru.v1as.tg.cat.jpa.entities.events;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
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
@DiscriminatorValue("CAT")
@NoArgsConstructor
@ToString(callSuper = true)
public class CatUserEvent extends UserEvent {
    @Column(nullable = false)
    private CatRequestVote result;

    @Column(nullable = false)
    private CatEventType catType;

    private String questName;

    @Column(nullable = false)
    private Integer messageId;

    public CatUserEvent(
            ChatEntity chat,
            UserEntity user,
            Integer messageId,
            CatEventType catType,
            CatRequestVote result) {
        this.chat = chat;
        this.user = user;
        this.messageId = messageId;
        this.catType = catType;
        this.result = result;
        this.date = LocalDateTime.now();
    }
}
