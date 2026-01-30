package com.twins.demo_twins.infrastructure.persistence.postgres.config;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@EnableJpaRepositories(
        basePackages = "com.twins.demo_twins.infrastructure.persistence.postgres.repository",
        entityManagerFactoryRef = "postgresEntityManagerFactory",
        transactionManagerRef = "postgresTransactionManager"
)
public class PostgresJpaConfig {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSourceProperties postgresDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource postgresDataSource() {
        return postgresDataSourceProperties()
                .initializeDataSourceBuilder()
                .build();
    }

    @Bean
    @Primary
    public LocalContainerEntityManagerFactoryBean postgresEntityManagerFactory(
            EntityManagerFactoryBuilder builder
    ) {
        return builder
                .dataSource(postgresDataSource())
                .packages("com.twins.demo_twins.infrastructure.persistence.postgres.entity")
                .persistenceUnit("postgres")
                .build();
    }

    @Bean
    @Primary
    public PlatformTransactionManager postgresTransactionManager(
            @Qualifier("postgresEntityManagerFactory")
            EntityManagerFactory emf
    ) {
        return new JpaTransactionManager(emf);
    }
}