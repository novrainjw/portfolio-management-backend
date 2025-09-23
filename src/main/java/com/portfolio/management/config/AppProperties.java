package com.portfolio.management.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Application-specific configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private Portfolio portfolio = new Portfolio();
    private Security security = new Security();
    private RateLimiting rateLimiting = new RateLimiting();
    private Audit audit = new Audit();

    // Getters and setters
    public Portfolio getPortfolio() {
        return portfolio;
    }

    public void setPortfolio(Portfolio portfolio) {
        this.portfolio = portfolio;
    }

    public Security getSecurity() {
        return security;
    }

    public void setSecurity(Security security) {
        this.security = security;
    }

    public RateLimiting getRateLimiting() {
        return rateLimiting;
    }

    public void setRateLimiting(RateLimiting rateLimiting) {
        this.rateLimiting = rateLimiting;
    }

    public Audit getAudit() {
        return audit;
    }

    public void setAudit(Audit audit) {
        this.audit = audit;
    }

    public static class Portfolio {
        private String defaultCurrency = "USD";
        private int maxPortfoliosPerUser = 10;
        private int maxHoldingsPerPortfolio = 100;
        private int priceUpdateInterval = 15;

        // Getters and setters
        public String getDefaultCurrency() {
            return defaultCurrency;
        }

        public void setDefaultCurrency(String defaultCurrency) {
            this.defaultCurrency = defaultCurrency;
        }

        public int getMaxPortfoliosPerUser() {
            return maxPortfoliosPerUser;
        }

        public void setMaxPortfoliosPerUser(int maxPortfoliosPerUser) {
            this.maxPortfoliosPerUser = maxPortfoliosPerUser;
        }

        public int getMaxHoldingsPerPortfolio() {
            return maxHoldingsPerPortfolio;
        }

        public void setMaxHoldingsPerPortfolio(int maxHoldingsPerPortfolio) {
            this.maxHoldingsPerPortfolio = maxHoldingsPerPortfolio;
        }

        public int getPriceUpdateInterval() {
            return priceUpdateInterval;
        }

        public void setPriceUpdateInterval(int priceUpdateInterval) {
            this.priceUpdateInterval = priceUpdateInterval;
        }
    }

    public static class Security {
        private Cors cors = new Cors();

        public Cors getCors() {
            return cors;
        }

        public void setCors(Cors cors) {
            this.cors = cors;
        }

        public static class Cors {
            private List<String> allowedOrigins;
            private String allowedMethods = "GET,POST,PUT,DELETE,PATCH,OPTIONS";
            private String allowedHeaders = "*";
            private boolean allowCredentials = true;
            private long maxAge = 3600;

            // Getters and setters
            public List<String> getAllowedOrigins() {
                return allowedOrigins;
            }

            public void setAllowedOrigins(List<String> allowedOrigins) {
                this.allowedOrigins = allowedOrigins;
            }

            public String getAllowedMethods() {
                return allowedMethods;
            }

            public void setAllowedMethods(String allowedMethods) {
                this.allowedMethods = allowedMethods;
            }

            public String getAllowedHeaders() {
                return allowedHeaders;
            }

            public void setAllowedHeaders(String allowedHeaders) {
                this.allowedHeaders = allowedHeaders;
            }

            public boolean isAllowCredentials() {
                return allowCredentials;
            }

            public void setAllowCredentials(boolean allowCredentials) {
                this.allowCredentials = allowCredentials;
            }

            public long getMaxAge() {
                return maxAge;
            }

            public void setMaxAge(long maxAge) {
                this.maxAge = maxAge;
            }
        }
    }

    public static class RateLimiting {
        private boolean enabled = true;
        private int requestsPerMinute = 100;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getRequestsPerMinute() {
            return requestsPerMinute;
        }

        public void setRequestsPerMinute(int requestsPerMinute) {
            this.requestsPerMinute = requestsPerMinute;
        }
    }

    public static class Audit {
        private boolean enabled = true;

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
