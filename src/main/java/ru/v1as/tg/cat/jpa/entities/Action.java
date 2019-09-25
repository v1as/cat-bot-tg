package ru.v1as.tg.cat.jpa.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

@Entity
@Data
public class Action<T> {

    @Id private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long chatId;

    @Column(nullable = false)
    private ActionType action;

    private String jsonData;

    @Transient private T data;

}
