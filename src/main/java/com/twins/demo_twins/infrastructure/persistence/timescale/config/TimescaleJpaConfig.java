package com.twins.demo_twins.infrastructure.persistence.timescale.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.twins.demo_twins.infrastructure.persistence.timescale.repository",
        entityManagerFactoryRef = "timescaleEntityManagerFactory",
        transactionManagerRef = "timescaleTransactionManager"
)
public class TimescaleJpaConfig {

    @Bean
    @ConfigurationProperties(prefix = "twins.timescale")
    public DataSourceProperties timescaleDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    public DataSource timescaleDataSource() {
        return timescaleDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean timescaleEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(timescaleDataSource())
                .packages("com.twins.demo_twins.infrastructure.persistence.timescale.entity")
                .persistenceUnit("timescale")
                .build();
    }

    @Bean
    public PlatformTransactionManager timescaleTransactionManager(
            @Qualifier("timescaleEntityManagerFactory")
            EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}
