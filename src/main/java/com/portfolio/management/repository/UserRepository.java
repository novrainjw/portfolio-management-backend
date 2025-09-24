package com.portfolio.management.repository;

import com.portfolio.management.entity.User;
import com.portfolio.management.repository.custom.UserRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String>, JpaSpecificationExecutor<User>, UserRepositoryCustom {
    // Find by unique fields
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // Find active users
    List<User> findByIsActiveTrue();

    List<User> findByIsActiveFalse();

    // Find by name patterns
    List<User> findByFirstNameContainingIgnoreCase(String firstName);

    List<User> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :fullName, '%))")
    List<User> findByFullNameContainingIgnoreCase(@Param("fullName") String fullName);

    // Find by date ranges
    List<User> findByCreatedAtBetween(Instant startDate, Instant endDate);

    List<User> findByLastLoginAtAfter(Instant after);

    List<User> findByLastLoginAtBefore(Instant before);

    // Users with portfolios
    @Query("SELECT DISTINCT u FROM User u JOIN u.portfolios p WHERE p.isActive = true")
    List<User> findUsersWithActivePortfolios();

    @Query("SELECT u FROM User u WHERE SIZE(u.portfolios) = 0")
    List<User> findUsersWithoutPortfolios();

    // Count queries
    long countByIsActiveTrue();

    @Query("SELECT COUNT(DISTINCT u) FROM User u JOIN u.portfolios p WHERE p.isActive = true")
    long countUsersWithActivePortfolios();

    // Recent activity
    @Query("SELECT u FROM User u WHERE u.lastLoginAt >= :since ORDER BY u.lastLoginAt DESC")
    List<User> findRecentlyActiveUsers(@Param("since") Instant since);

    // User statistics
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.portfolios p WHERE u.id = :userId")
    Optional<User> findByIdWithPortfolios(@Param("userId") String userId);

    @Query("SELECT u FROM User u LEFT JOIN FETCH u.watchlistItems w WHERE u.id = :userId")
    Optional<User> findByIdWithWatchlist(@Param("userId") String userId);

    // Batch operations
    @Query("UPDATE User u SET u.isActive = false WHERE u.lastLoginAt < :cutoffDate")
    int deactivateInactiveUsers(@Param("cutoffDate") Instant cutoffDate);

    @Query("SELECT u FROM User u WHERE " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
}