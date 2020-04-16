package ru.v1as.tg.cat.jpa.entities.chat;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import ru.v1as.tg.cat.service.ChatParam;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"chat_id", "param"}))
public class ChatParamValue extends ParamValue {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated private ChatParam param;

    public ChatParamValue(
            @NonNull ChatParam param, @NonNull ChatEntity chat, @NonNull String value) {
        this.chat = chat;
        this.param = param;
        this.value = value;
    }

}
