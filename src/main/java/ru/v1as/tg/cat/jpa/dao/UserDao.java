package ru.v1as.tg.cat.jpa.dao;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.v1as.tg.cat.jpa.entities.user.UserEntity;

public interface UserDao extends JpaRepository<UserEntity, Integer> {

    Optional<UserEntity> findByUserName(String adminUserName);
}
