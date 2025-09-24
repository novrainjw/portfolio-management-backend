package com.portfolio.management.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponse {
    private String id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String fullName;
    private boolean isActive;
    private boolean emailVerified;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant updatedAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", timezone = "UTC")
    private Instant lastLoginAt;

    // Statistics (optional fields)
    private Integer portfolioCount;
    private Integer activePortfolioCount;
    private Integer watchlistItemCount;

    // Constructors
    public UserResponse() {
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UserResponse userResponse;

        public Builder() {
            this.userResponse = new UserResponse();
        }

        public Builder id(String id) {
            userResponse.id = id;
            return this;
        }

        public Builder username(String username) {
            userResponse.username = username;
            return this;
        }

        public Builder email(String email) {
            userResponse.email = email;
            return this;
        }

        public Builder firstName(String firstName) {
            userResponse.firstName = firstName;
            return this;
        }

        public Builder lastName(String lastName) {
            userResponse.lastName = lastName;
            return this;
        }

        public Builder fullName(String fullName) {
            userResponse.fullName = fullName;
            return this;
        }

        public Builder isActive(boolean isActive) {
            userResponse.isActive = isActive;
            return this;
        }

        public Builder emailVerified(boolean emailVerified) {
            userResponse.emailVerified = emailVerified;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            userResponse.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            userResponse.updatedAt = updatedAt;
            return this;
        }

        public Builder lastLoginAt(Instant lastLoginAt) {
            userResponse.lastLoginAt = lastLoginAt;
            return this;
        }

        public Builder portfolioCount(Integer portfolioCount) {
            userResponse.portfolioCount = portfolioCount;
            return this;
        }

        public Builder activePortfolioCount(Integer activePortfolioCount) {
            userResponse.activePortfolioCount = activePortfolioCount;
            return this;
        }

        public Builder watchlistItemCount(Integer watchlistItemCount) {
            userResponse.watchlistItemCount = watchlistItemCount;
            return this;
        }

        public UserResponse build() {
            if (userResponse.firstName != null && userResponse.lastName != null) {
                userResponse.fullName = userResponse.firstName + " " + userResponse.lastName;
            }
            return userResponse;
        }
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return fullName != null ? fullName :
                (firstName != null && lastName != null ? firstName + " " + lastName : null);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Integer getPortfolioCount() {
        return portfolioCount;
    }

    public void setPortfolioCount(Integer portfolioCount) {
        this.portfolioCount = portfolioCount;
    }

    public Integer getActivePortfolioCount() {
        return activePortfolioCount;
    }

    public void setActivePortfolioCount(Integer activePortfolioCount) {
        this.activePortfolioCount = activePortfolioCount;
    }

    public Integer getWatchlistItemCount() {
        return watchlistItemCount;
    }

    public void setWatchlistItemCount(Integer watchlistItemCount) {
        this.watchlistItemCount = watchlistItemCount;
    }
}