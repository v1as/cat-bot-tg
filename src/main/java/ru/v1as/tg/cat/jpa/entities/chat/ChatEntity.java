package ru.v1as.tg.cat.jpa.entities.chat;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;
import ru.v1as.tg.cat.model.TgChat;

@Entity
@Data
@ToString(exclude = "users")
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class ChatEntity implements TgChat {
    @Id private Long id;
    private String title;
    private String description;
    private LocalDateTime updated;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserEntity> users;

    public List<UserEntity> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public boolean update(TgChat chat, UserEntity user) {
        boolean changed = false;
        if (Objects.equals(title, chat.getTitle())) {
            title = chat.getTitle();
            changed = true;
        }
        if (Objects.equals(description, chat.getDescription())) {
            description = chat.getDescription();
            changed = true;
        }
        if (!this.getUsers().stream()
                .map(UserEntity::getId)
                .collect(Collectors.toSet())
                .contains(user.getId())) {
            this.getUsers().add(user);
            changed = true;
        }
        if (changed) {
            updated = LocalDateTime.now();
        }
        return changed;
    }

    @Override
    public Boolean isUserChat() {
        return false;
    }
}
