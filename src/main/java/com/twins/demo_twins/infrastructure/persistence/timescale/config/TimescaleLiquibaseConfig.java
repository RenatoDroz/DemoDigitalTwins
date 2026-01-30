package com.twins.demo_twins.infrastructure.persistence.timescale.config;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class TimescaleLiquibaseConfig {

    @Bean
    public SpringLiquibase timescaleLiquibase(
            @Qualifier("timescaleDataSource") DataSource dataSource
    ) {
        SpringLiquibase liquibase = new SpringLiquibase();

        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:/db/changelog/timescale/changelog-master.yaml");
        liquibase.setShouldRun(true);

        return liquibase;
    }
}
