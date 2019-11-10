package ru.v1as.tg.cat.jpa.entities.user;

import static org.apache.http.util.TextUtils.isEmpty;

import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.Entity;
import javax.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {
    @Id private Integer id;
    private String username;
    private String firstName;
    private String lastName;
    private String languageCode;
    private Boolean privateChat;

    public String getUsernameOrFullName() {
        if (isEmpty(username)) {
            return Stream.of(firstName, lastName)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
        } else {
            return "@" + username;
        }
    }
}
