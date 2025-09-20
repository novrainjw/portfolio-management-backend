package com.portfolio.management.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_created_at", columnList = "created_at")
})
public class User extends BaseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", length = 50)
    private String id;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 100, message = "Username must be between 3 and 100 characters")
    @Column(name = "username", nullable = false, unique = true, length = 100)
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Column(name = "password", nullable = false)
    private String password;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 100, message = "First name must be between 1 and 100 characters")
    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 100, message = "Last name must be between 1 and 100 characters")
    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "last_login_at")
    private Instant lastLoginAt;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Portfolio> portfolios = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Watchlist> watchlistItems = new HashSet<>();

    // Constructors
    public User() {}

    public User(String username, String password, String email, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // Business Methods
    public String getFullName() {
        return firstName + " " + lastName;
    }

    public boolean isAccountActive() {
        return Boolean.TRUE.equals(isActive);
    }

    public void activate() {
        this.isActive = true;
    }

    public void deactivate() {
        this.isActive = false;
    }

    public void updateLastLogin() {
        this.lastLoginAt = Instant.now();
    }

    // Helper methods for bidirectional relationships
    public void addPortfolio(Portfolio portfolio) {
        portfolios.add(portfolio);
        portfolio.setUser(this);
    }

    public void removePortfolio(Portfolio portfolio) {
        portfolios.remove(portfolio);
        portfolio.setUser(null);
    }

    public void addWatchlistItem(Watchlist watchlistItem) {
        watchlistItems.add(watchlistItem);
        watchlistItem.setUser(this);
    }

    public void removeWatchlistItem(Watchlist watchlistItem) {
        watchlistItems.remove(watchlistItem);
        watchlistItem.setUser(null);
    }

    // Getters and Setters
    @Override
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public void setLastLoginAt(Instant lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Set<Portfolio> getPortfolios() {
        return portfolios;
    }

    public void setPortfolios(Set<Portfolio> portfolios) {
        this.portfolios = portfolios;
    }

    public Set<Watchlist> getWatchlistItems() {
        return watchlistItems;
    }

    public void setWatchlistItems(Set<Watchlist> watchlistItems) {
        this.watchlistItems = watchlistItems;
    }
}
