package ru.v1as.tg.cat.jpa.entities.resource;

import static javax.persistence.EnumType.STRING;

import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceEntity {
    @Id private Long id;

    private String name;

    @Enumerated(STRING)
    private ResourceType type;

    private String unit;
}
