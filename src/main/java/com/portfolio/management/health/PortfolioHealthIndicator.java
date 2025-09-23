package com.portfolio.management.health;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
public class PortfolioHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        // Add custom health checks here
        try {
            // Example: Check database connectivity, external service availability, etc.
            return Health.up()
                    .withDetail("status", "Portfolio Management System is running")
                    .withDetail("version", "1.0.0")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}
