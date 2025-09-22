package com.portfolio.management;

import com.portfolio.management.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.export.appoptics.AppOpticsProperties;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties({AppProperties.class})
@EnableJpaAuditing
@EnableTransactionManagement
@EnableCaching
@EnableAsync
@EnableScheduling
public class PortfolioManagementBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(PortfolioManagementBackendApplication.class, args);
    }

}
