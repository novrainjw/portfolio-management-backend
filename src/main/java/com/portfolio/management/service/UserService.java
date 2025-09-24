package com.portfolio.management.service;

import com.portfolio.management.dto.request.UserRegistrationRequest;
import com.portfolio.management.dto.request.UserUpdateRequest;
import com.portfolio.management.dto.response.UserResponse;
import com.portfolio.management.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * User Service Interface
 */
public interface UserService {

    // CRUD Operations
    UserResponse createUser(UserRegistrationRequest request);

    UserResponse updateUser(String userId, UserUpdateRequest request);

    UserResponse getCurrentUser();

    UserResponse getUserById(String userId);

    UserResponse getUserByUsername(String username);

    UserResponse getUserByEmail(String email);

    Page<UserResponse> getAllUsers(Pageable pageable);

    void deleteUser(String userId);

    void deactivateUser(String userId);

    void activateUser(String userId);

    // Authentication & Password
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    void changePassword(String userId, String oldPassword, String newPassword);

    void resetPassword(String email);

    void updateLastLoginTime(String userId);

    // User Statistics
    long getTotalUserCount();

    long getActiveUserCount();

    List<UserResponse> getRecentlyActiveUsers(Instant since);

    List<UserResponse> getUsersWithActivePortfolios();

    List<UserResponse> getUsersWithoutPortfolios();

    // Search & Filter
    List<UserResponse> searchUsers(String searchTerm);

    List<UserResponse> getUsersByNamePattern(String namePattern);

    Page<UserResponse> getUsersWithFilters(String searchTerm, Boolean isActive,
                                           Instant createdAfter, Instant lastLoginAfter,
                                           Pageable pageable);

    // User Profile
    UserResponse updateProfile(String userId, String firstName, String lastName);

    UserResponse uploadProfilePicture(String userId, String pictureUrl);

    // Account Management
    void lockUser(String userId, String reason);

    void unlockUser(String userId);

    boolean isUserLocked(String userId);

    void markEmailAsVerified(String userId);

    // Utility Methods
    User getUserEntityById(String userId);

    User getUserEntityByUsername(String username);

    Optional<User> findUserEntityById(String userId);

    boolean isCurrentUser(String userId);

    void validateUserAccess(String userId);
}