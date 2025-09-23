package com.portfolio.management.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.portfolio.management.repository")
@EnableJpaAuditing
@EnableTransactionManagement
public class JpaConfig {
    // JPA Auditing is now enabled for BaseEntity @CreatedDate and @LastModifiedDate
}
