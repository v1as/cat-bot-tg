package ru.v1as.tg.cat.config;

import java.sql.SQLException;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Slf4j
@EnableJpaRepositories("ru.v1as.tg.cat.jpa.dao")
@EntityScan("ru.v1as.tg.cat.jpa.entities")
@Import({DataSourceAutoConfiguration.class, HibernateJpaAutoConfiguration.class})
@Configuration
@EnableTransactionManagement
public class JpaConfiguration {

    @Profile("prod")
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server h2Server() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9092");
    }

}
