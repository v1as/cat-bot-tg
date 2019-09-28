package ru.v1as.tg.cat.conf;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@Profile("!test")
@Configuration
@EnableJpaRepositories("ru.v1as.tg.cat.jpa")
@EntityScan("ru.v1as.tg.cat.jpa.entities")
@Import({
    DataSourceAutoConfiguration.class,
    HibernateJpaAutoConfiguration.class,
})
public class JpaConfiguration {

    @PostConstruct
    public void init() {
        log.info("=============================================================");
    }
}
