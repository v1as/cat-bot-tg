package ru.v1as.tg.cat.jpa.entities.resource;

import javax.persistence.Entity;
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
    private boolean isUnique;
    private String unit;
}
