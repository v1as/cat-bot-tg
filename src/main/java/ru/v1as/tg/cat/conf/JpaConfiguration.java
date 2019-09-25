package ru.v1as.tg.cat.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Profile("!test")
@Configuration
@EnableJpaRepositories("ru.v1as.tg.cat.jpa")
public class JpaConfiguration {

}
