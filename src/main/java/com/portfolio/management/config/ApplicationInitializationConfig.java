package com.portfolio.management.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Application initialization and startup configuration
 */
@Configuration
public class ApplicationInitializationConfig {
    private static final Logger logger = LoggerFactory.getLogger(ApplicationInitializationConfig.class);

    @Bean
    @Profile("dev")
    @ConditionalOnProperty(name = "app.initialization.enabled", havingValue = "true", matchIfMissing = true)
    public CommandLineRunner initializeApplication() {
        return args -> {
            logger.info("=================================================");
            logger.info("Portfolio Management Backend Application Started");
            logger.info("=================================================");
            logger.info("Environment: Development");
            logger.info("Swagger UI: http://localhost:8080/api/v1/swagger-ui.html");
            logger.info("API Docs: http://localhost:8080/api/v1/api-docs");
            logger.info("Actuator: http://localhost:8080/api/v1/actuator");
            logger.info("=================================================");
        };
    }

    @Bean
    @Profile("prod")
    public CommandLineRunner initializeProductionApplication() {
        return args -> {
            logger.info("Portfolio Management Backend Application Started in Production Mode");
            logger.info("Version: 1.0.0");
        };
    }
}
