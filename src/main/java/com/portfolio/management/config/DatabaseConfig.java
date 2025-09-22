package com.portfolio.management.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.Clock;

/**
 * Database and JPA configuration
 */
@Configuration
@EnableJpaRepositories(basePackages = "com.portfolio.management.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class DatabaseConfig {
    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }
}
