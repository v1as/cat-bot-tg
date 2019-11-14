package ru.v1as.tg.cat.jpa.entities.events;

import static ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote.CAT1;
import static ru.v1as.tg.cat.jpa.entities.events.CatEventType.CURIOS_CAT;
import static ru.v1as.tg.cat.jpa.entities.events.UserEventType.CAT;

import java.time.LocalDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.v1as.tg.cat.callbacks.is_cat.CatRequestVote;
import ru.v1as.tg.cat.jpa.entities.chat.ChatEntity;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("CAT")
@NoArgsConstructor
public class CatUserEvent extends UserEvent {
    private CatRequestVote result;
    private CatEventType catType;
    private String questName;
    private Integer messageId;

    {
        setType(CAT);
        setDate(LocalDateTime.now());
    }

    public static CatUserEvent curiosCat(ChatEntity chat, UserEntity user, Message voteMessage) {
        final CatUserEvent event = new CatUserEvent();
        event.setChat(chat);
        event.setUser(user);
        event.setMessageId(voteMessage.getMessageId());
        event.setCatType(CURIOS_CAT);
        event.setResult(CAT1);
        return event;
    }
}
