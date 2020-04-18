package ru.v1as.tg.cat.jpa.entities.events;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.service.ChatParam;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CP")
@NoArgsConstructor
@ToString(callSuper = true)
public class ChatParamChangeEvent extends UserEvent {

    @Enumerated private ChatParam param;

    @Column(nullable = false, length = 20)
    protected String oldValue;

    @Column(nullable = false, length = 20)
    protected String newValue;

    public ChatParamChangeEvent(
            ChatEntity chat, UserEntity user, ChatParam param, String oldValue, String newValue) {
        this.chat = chat;
        this.user = user;
        this.param = param;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.date = LocalDateTime.now();
    }
}
