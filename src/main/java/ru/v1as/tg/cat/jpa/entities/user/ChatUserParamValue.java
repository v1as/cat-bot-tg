package ru.v1as.tg.cat.jpa.entities.user;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.chat.ParamValue;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "user_id", "param"}))
public class ChatUserParamValue extends ParamValue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @ManyToOne(optional = false)
    protected UserEntity user;

    @Enumerated private ChatUserParam param;

    public ChatUserParamValue(
            @NonNull ChatEntity chat,
            @NonNull UserEntity user,
            @NonNull ChatUserParam param,
            @NonNull Object value) {
        this.chat = chat;
        this.user = user;
        this.param = param;
        this.value = value.toString();
    }
}
