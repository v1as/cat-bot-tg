package ru.v1as.tg.cat.jpa.entities.chat;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

@Entity
@Data
@EqualsAndHashCode(of = "id")
public class PublicChatEntity {
    @Id private Long id;
    private String title;
    private String description;
    private Integer membersAmount;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<UserEntity> users;

    public List<UserEntity> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }
}
