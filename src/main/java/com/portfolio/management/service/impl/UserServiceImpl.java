package com.portfolio.management.service.impl;

import com.portfolio.management.dto.request.UserRegistrationRequest;
import com.portfolio.management.dto.request.UserUpdateRequest;
import com.portfolio.management.dto.response.UserResponse;
import com.portfolio.management.entity.User;
import com.portfolio.management.exception.BadRequestException;
import com.portfolio.management.exception.ResourceNotFoundException;
import com.portfolio.management.mapper.UserMapper;
import com.portfolio.management.repository.UserRepository;
import com.portfolio.management.security.UserPrincipal;
import com.portfolio.management.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.portfolio.management.constants.AppConstants.CACHE_USERS;

/**
 * User Service Implementation
 */
@Service
@Transactional
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public UserResponse createUser(UserRegistrationRequest request) {
        logger.info("Creating new user with username: {}", request.getUsername());

        // Validation
        if (existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username '" + request.getUsername() + "' is already taken");
        }

        if (existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
        }

        // Create user entity
        User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
//        user.setAccountActive(true);
        user.activate();
//        user.setEmailVerified(false);
        user.setCreatedAt(Instant.now());

        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public UserResponse updateUser(String userId, UserUpdateRequest request) {
        logger.info("Updating user: {}", userId);

        User user = getUserEntityById(userId);
        validateUserAccess(userId);

        // Update fields
        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            if (existsByEmail(request.getEmail())) {
                throw new BadRequestException("Email '" + request.getEmail() + "' is already registered");
            }
            user.setEmail(request.getEmail());
//            user.setEmailVerified(false); // Reset email verification
        }

        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);

        logger.info("User updated successfully: {}", userId);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        UserPrincipal currentUser = getCurrentUserPrincipal();
        User user = getUserEntityById(currentUser.getId());
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CACHE_USERS, key = "#userId")
    public UserResponse getUserById(String userId) {
        User user = getUserEntityById(userId);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = getUserEntityByUsername(username);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public void deleteUser(String userId) {
        logger.info("Deleting user: {}", userId);

        User user = getUserEntityById(userId);

        // Soft delete by deactivating
//        user.setAccountActive(false);
        user.deactivate();
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        logger.info("User deleted (deactivated): {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public void deactivateUser(String userId) {
        logger.info("Deactivating user: {}", userId);

        User user = getUserEntityById(userId);
//        user.setAccountActive(false);
        user.deactivate();
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        logger.info("User deactivated: {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public void activateUser(String userId) {
        logger.info("Activating user: {}", userId);

        User user = getUserEntityById(userId);
//        user.setAccountActive(true);
        user.activate();
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        logger.info("User activated: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public void changePassword(String userId, String oldPassword, String newPassword) {
        logger.info("Changing password for user: {}", userId);

        User user = getUserEntityById(userId);
        validateUserAccess(userId);

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        logger.info("Password changed successfully for user: {}", userId);
    }

    @Override
    @Transactional
    public void resetPassword(String email) {
        logger.info("Password reset requested for email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));

        // In a real application, you would:
        // 1. Generate a secure reset token
        // 2. Send email with reset link
        // 3. Store token with expiration

        logger.info("Password reset initiated for user: {}", user.getId());
        // TODO: Implement email service integration
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public void updateLastLoginTime(String userId) {
        User user = getUserEntityById(userId);
        user.setLastLoginAt(Instant.now());
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public long getTotalUserCount() {
        return userRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long getActiveUserCount() {
        return userRepository.countByIsActiveTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getRecentlyActiveUsers(Instant since) {
        List<User> users = userRepository.findRecentlyActiveUsers(since);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersWithActivePortfolios() {
        List<User> users = userRepository.findUsersWithActivePortfolios();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersWithoutPortfolios() {
        List<User> users = userRepository.findUsersWithoutPortfolios();
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String searchTerm) {
        List<User> users = userRepository.searchUsers(searchTerm);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUsersByNamePattern(String namePattern) {
        List<User> users = userRepository.findByFullNameContainingIgnoreCase(namePattern);
        return users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> getUsersWithFilters(String searchTerm, Boolean isActive,
                                                  Instant createdAfter, Instant lastLoginAfter,
                                                  Pageable pageable) {

        Specification<User> spec = Specification.where(null);

        if (searchTerm != null && !searchTerm.isEmpty()) {
            spec = spec.and((root, query, cb) -> {
                String pattern = "%" + searchTerm.toLowerCase() + "%";
                return cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("firstName")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern)
                );
            });
        }

        if (isActive != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("accountActive"), isActive));
        }

        if (createdAfter != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), createdAfter));
        }

        if (lastLoginAfter != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("lastLoginAt"), lastLoginAfter));
        }

        Page<User> users = userRepository.findAll(spec, pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public UserResponse updateProfile(String userId, String firstName, String lastName) {
        User user = getUserEntityById(userId);
        validateUserAccess(userId);

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public UserResponse uploadProfilePicture(String userId, String pictureUrl) {
        User user = getUserEntityById(userId);
        validateUserAccess(userId);

        // TODO: Implement profile picture storage logic
        user.setUpdatedAt(Instant.now());

        User updatedUser = userRepository.save(user);
        return userMapper.toResponse(updatedUser);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    @PreAuthorize("hasRole('ADMIN')")
    public void lockUser(String userId, String reason) {
        logger.info("Locking user: {} for reason: {}", userId, reason);

        User user = getUserEntityById(userId);
//        user.setAccountActive(false);
        user.activate();
        user.setUpdatedAt(Instant.now());
        // TODO: Add lock reason field to User entity
        userRepository.save(user);

        logger.info("User locked: {}", userId);
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    @PreAuthorize("hasRole('ADMIN')")
    public void unlockUser(String userId) {
        logger.info("Unlocking user: {}", userId);

        User user = getUserEntityById(userId);
//        user.setAccountActive(true);
        user.activate();
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);

        logger.info("User unlocked: {}", userId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isUserLocked(String userId) {
        User user = getUserEntityById(userId);
        return !user.isAccountActive();
    }

    @Override
    @Transactional
    @CacheEvict(value = CACHE_USERS, key = "#userId")
    public void markEmailAsVerified(String userId) {
        User user = getUserEntityById(userId);
//        user.setEmailVerified(true);
        user.setUpdatedAt(Instant.now());
        userRepository.save(user);
    }

    // Utility Methods

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserEntityByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findUserEntityById(String userId) {
        return userRepository.findById(userId);
    }

    @Override
    public boolean isCurrentUser(String userId) {
        try {
            UserPrincipal currentUser = getCurrentUserPrincipal();
            return currentUser.getId().equals(userId);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public void validateUserAccess(String userId) {
        if (!isCurrentUser(userId)) {
            // Check if user has admin role
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || !auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
                throw new BadRequestException("Access denied: You can only access your own data");
            }
        }
    }

    // Helper Methods

    private UserPrincipal getCurrentUserPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
            throw new BadRequestException("No authenticated user found");
        }
        return (UserPrincipal) authentication.getPrincipal();
    }
}