package com.portfolio.management.mapper;

import com.portfolio.management.dto.request.UserRegistrationRequest;
import com.portfolio.management.dto.request.UserUpdateRequest;
import com.portfolio.management.dto.response.UserResponse;
import com.portfolio.management.entity.User;
import org.mapstruct.*;

import java.time.Instant;
import java.util.List;

/**
 * MapStruct mapper for User entity and DTOs
 */
@Mapper(
        componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        injectionStrategy = InjectionStrategy.CONSTRUCTOR
)
public interface UserMapper {

    /**
     * Convert User entity to UserResponse DTO
     */
    @Mapping(target = "fullName", expression = "java(buildFullName(user.getFirstName(), user.getLastName()))")
    @Mapping(target = "isActive", source = "accountActive")
    @Mapping(target = "portfolioCount", ignore = true)
    @Mapping(target = "activePortfolioCount", ignore = true)
    @Mapping(target = "watchlistItemCount", ignore = true)
    UserResponse toResponse(User user);

    /**
     * Convert UserRegistrationRequest to User entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // Will be encoded separately
    @Mapping(target = "accountActive", constant = "true")
    @Mapping(target = "emailVerified", constant = "false")
    @Mapping(target = "createdAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "portfolios", ignore = true)
    @Mapping(target = "watchlistItems", ignore = true)
    User toEntity(UserRegistrationRequest request);

    /**
     * Update User entity from UserUpdateRequest
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "username", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountActive", ignore = true)
    @Mapping(target = "emailVerified", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", expression = "java(java.time.Instant.now())")
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "portfolios", ignore = true)
    @Mapping(target = "watchlistItems", ignore = true)
    void updateEntityFromRequest(UserUpdateRequest request, @MappingTarget User user);

    /**
     * Convert list of User entities to list of UserResponse DTOs
     */
    List<UserResponse> toResponseList(List<User> users);

    /**
     * Convert UserResponse to User entity (for specific use cases)
     */
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "accountActive", source = "active")
    @Mapping(target = "portfolios", ignore = true)
    @Mapping(target = "watchlistItems", ignore = true)
    User toEntity(UserResponse response);

    /**
     * Create UserResponse with statistics
     */
    @Mapping(target = "fullName", expression = "java(buildFullName(user.getFirstName(), user.getLastName()))")
    @Mapping(target = "isActive", source = "user.accountActive")
    @Mapping(target = "portfolioCount", source = "portfolioCount")
    @Mapping(target = "activePortfolioCount", source = "activePortfolioCount")
    @Mapping(target = "watchlistItemCount", source = "watchlistItemCount")
    UserResponse toResponseWithStats(User user, Integer portfolioCount, Integer activePortfolioCount, Integer watchlistItemCount);

    /**
     * Create minimal UserResponse (for lists and performance)
     */
    @Mapping(target = "fullName", expression = "java(buildFullName(user.getFirstName(), user.getLastName()))")
    @Mapping(target = "isActive", source = "accountActive")
    @Mapping(target = "portfolioCount", ignore = true)
    @Mapping(target = "activePortfolioCount", ignore = true)
    @Mapping(target = "watchlistItemCount", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserResponse toMinimalResponse(User user);

    // Helper methods for complex mappings

    /**
     * Build full name from first and last name
     */
    default String buildFullName(String firstName, String lastName) {
        if (firstName == null && lastName == null) {
            return null;
        }
        if (firstName == null) {
            return lastName;
        }
        if (lastName == null) {
            return firstName;
        }
        return firstName + " " + lastName;
    }

    /**
     * Map Instant to formatted string if needed
     */
    default String instantToString(Instant instant) {
        return instant != null ? instant.toString() : null;
    }

    /**
     * Parse string to Instant if needed
     */
    default Instant stringToInstant(String instantString) {
        return instantString != null ? Instant.parse(instantString) : null;
    }

    /**
     * Before mapping method to perform any pre-processing
     */
    @BeforeMapping
    default void beforeMapping(@MappingTarget UserResponse.Builder builder, User user) {
        // Any pre-processing logic can be added here
        if (user != null && builder != null) {
            // Example: Add computed fields or validation
        }
    }

    /**
     * After mapping method to perform any post-processing
     */
    @AfterMapping
    default void afterMapping(@MappingTarget UserResponse userResponse, User user) {
        // Any post-processing logic can be added here
        if (userResponse != null && user != null) {
            // Ensure full name is set correctly
            if (userResponse.getFullName() == null) {
                userResponse.setFullName(buildFullName(user.getFirstName(), user.getLastName()));
            }
        }
    }

    /**
     * Custom mapping for password field (typically ignored for security)
     */
    @Named("ignorePassword")
    default String ignorePassword(String password) {
        return null; // Never map password to response DTOs
    }

    /**
     * Custom mapping for sensitive data
     */
    @Named("maskEmail")
    default String maskEmail(String email) {
        if (email == null || email.length() < 3) {
            return email;
        }
        int atIndex = email.indexOf('@');
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }
}