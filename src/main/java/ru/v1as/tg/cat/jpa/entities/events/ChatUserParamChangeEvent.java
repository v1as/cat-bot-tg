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
import ru.v1as.tg.cat.jpa.entities.user.ChatUserParam;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CUP")
@NoArgsConstructor
@ToString(callSuper = true)
public class ChatUserParamChangeEvent extends UserEvent {

    @Enumerated private ChatUserParam param;

    @Column(nullable = false, length = 20)
    protected String oldValue;

    @Column(nullable = false, length = 20)
    protected String newValue;

    public ChatUserParamChangeEvent(
            ChatEntity chat,
            UserEntity user,
            ChatUserParam param,
            String oldValue,
            String newValue) {
        this.chat = chat;
        this.user = user;
        this.param = param;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.date = LocalDateTime.now();
    }
}
