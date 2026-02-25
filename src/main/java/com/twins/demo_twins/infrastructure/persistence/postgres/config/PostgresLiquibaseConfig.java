package com.twins.demo_twins.infrastructure.persistence.postgres.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class PostgresLiquibaseConfig {

    @Bean
    public SpringLiquibase postgresLiquibase(
            @Qualifier("postgresDataSource") DataSource dataSource
    ) {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:/db/changelog/postgres/changelog-master.yaml");
        liquibase.setShouldRun(true);
        return liquibase;
    }
}
