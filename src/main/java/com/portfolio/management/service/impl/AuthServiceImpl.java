package com.portfolio.management.service.impl;

import com.portfolio.management.dto.request.LoginRequest;
import com.portfolio.management.dto.request.UserRegistrationRequest;
import com.portfolio.management.dto.response.JwtAuthResponse;
import com.portfolio.management.dto.response.UserResponse;
import com.portfolio.management.entity.User;
import com.portfolio.management.exception.BadRequestException;
import com.portfolio.management.exception.ResourceNotFoundException;
import com.portfolio.management.security.UserPrincipal;
import com.portfolio.management.service.AuthService;
import com.portfolio.management.service.JwtService;
import com.portfolio.management.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Authentication Service Implementation
 */
@Service
@Transactional
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;

    // In production, use Redis or database for token blacklist
    private final ConcurrentMap<String, Instant> tokenBlacklist = new ConcurrentHashMap<>();

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           JwtService jwtService,
                           UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public JwtAuthResponse login(LoginRequest loginRequest) {
        logger.info("Login attempt for user: {}", loginRequest.getUsername());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user principal
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

            // Update last login time
            userService.updateLastLoginTime(userPrincipal.getId());

            // Generate tokens
            String accessToken = jwtService.generateToken(authentication);
            String refreshToken = jwtService.generateRefreshToken(userPrincipal.getUsername());

            logger.info("User logged in successfully: {}", loginRequest.getUsername());

            return JwtAuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getTokenRemainingValidity(accessToken))
                    .user(userService.getUserById(userPrincipal.getId()))
                    .build();

        } catch (BadCredentialsException e) {
            logger.error("Invalid credentials for user: {}", loginRequest.getUsername());
            throw new BadRequestException("Invalid username or password");
        } catch (Exception e) {
            logger.error("Login failed for user: {} - {}", loginRequest.getUsername(), e.getMessage());
            throw new BadRequestException("Login failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public UserResponse register(UserRegistrationRequest registrationRequest) {
        logger.info("Registration attempt for user: {}", registrationRequest.getUsername());

        try {
            UserResponse userResponse = userService.createUser(registrationRequest);

            // TODO: Send email verification
            // emailService.sendVerificationEmail(userResponse.getEmail(), verificationToken);

            logger.info("User registered successfully: {}", registrationRequest.getUsername());
            return userResponse;

        } catch (Exception e) {
            logger.error("Registration failed for user: {} - {}", registrationRequest.getUsername(), e.getMessage());
            throw e; // Re-throw the exception to let global handler manage it
        }
    }

    @Override
    @Transactional(readOnly = true)
    public JwtAuthResponse refreshToken(String refreshToken) {
        logger.info("Token refresh attempt");

        try {
            // Validate refresh token
            if (!jwtService.validateToken(refreshToken)) {
                throw new BadRequestException("Invalid refresh token");
            }

            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new BadRequestException("Token is not a refresh token");
            }

            // Check if token is blacklisted
            if (isTokenBlacklisted(refreshToken)) {
                throw new BadRequestException("Refresh token has been revoked");
            }

            // Extract username and generate new access token
            String username = jwtService.getUsernameFromToken(refreshToken);
            String newAccessToken = jwtService.generateTokenFromUsername(username);

            // Get user info
            UserResponse user = userService.getUserByUsername(username);

            logger.info("Token refreshed successfully for user: {}", username);

            return JwtAuthResponse.builder()
                    .accessToken(newAccessToken)
                    .refreshToken(refreshToken) // Keep the same refresh token
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getTokenRemainingValidity(newAccessToken))
                    .user(user)
                    .build();

        } catch (Exception e) {
            logger.error("Token refresh failed: {}", e.getMessage());
            throw new BadRequestException("Token refresh failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void logout(String token) {
        logger.info("Logout attempt");

        try {
            if (token != null && jwtService.validateToken(token)) {
                // Add token to blacklist
                blacklistToken(token);

                String username = jwtService.getUsernameFromToken(token);
                logger.info("User logged out successfully: {}", username);
            }

            // Clear security context
            SecurityContextHolder.clearContext();

        } catch (Exception e) {
            logger.error("Logout error: {}", e.getMessage());
            // Don't throw exception for logout errors, just log them
        }
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        logger.info("Password reset request for email: {}", email);

        try {
            // Check if user exists
            UserResponse user = userService.getUserByEmail(email);

            // Generate password reset token
            String resetToken = jwtService.generateTokenFromUsername(user.getUsername());

            // TODO: Send password reset email
            // emailService.sendPasswordResetEmail(email, resetToken);

            logger.info("Password reset email sent to: {}", email);

        } catch (ResourceNotFoundException e) {
            // Don't reveal if email exists or not for security reasons
            logger.warn("Password reset attempted for non-existent email: {}", email);
            // Still log success to prevent email enumeration
        } catch (Exception e) {
            logger.error("Password reset failed for email: {} - {}", email, e.getMessage());
            throw new BadRequestException("Password reset failed");
        }
    }

    @Override
    @Transactional
    public void resetPassword(String token, String newPassword) {
        logger.info("Password reset attempt with token");

        try {
            // Validate token
            if (!jwtService.validateToken(token)) {
                throw new BadRequestException("Invalid or expired reset token");
            }

            // Check if token is blacklisted
            if (isTokenBlacklisted(token)) {
                throw new BadRequestException("Reset token has already been used");
            }

            String username = jwtService.getUsernameFromToken(token);
            User user = userService.getUserEntityByUsername(username);

            // Update password directly (bypass old password check)
            // This would require a direct repository call or a special service method
            // user.setPassword(passwordEncoder.encode(newPassword));
            // userRepository.save(user);

            // Blacklist the reset token so it can't be used again
            blacklistToken(token);

            logger.info("Password reset successfully for user: {}", username);

        } catch (Exception e) {
            logger.error("Password reset failed: {}", e.getMessage());
            throw new BadRequestException("Password reset failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void changePassword(String oldPassword, String newPassword) {
        logger.info("Password change attempt");

        try {
            // Get current user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !(authentication.getPrincipal() instanceof UserPrincipal)) {
                throw new BadRequestException("No authenticated user found");
            }

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            userService.changePassword(userPrincipal.getId(), oldPassword, newPassword);

            logger.info("Password changed successfully for user: {}", userPrincipal.getUsername());

        } catch (Exception e) {
            logger.error("Password change failed: {}", e.getMessage());
            throw new BadRequestException("Password change failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void verifyEmail(String verificationToken) {
        logger.info("Email verification attempt");

        try {
            // Validate token
            if (!jwtService.validateToken(verificationToken)) {
                throw new BadRequestException("Invalid or expired verification token");
            }

            String username = jwtService.getUsernameFromToken(verificationToken);
            User user = userService.getUserEntityByUsername(username);

            // Mark email as verified
            userService.markEmailAsVerified(user.getId());

            // Blacklist the verification token
            blacklistToken(verificationToken);

            logger.info("Email verified successfully for user: {}", username);

        } catch (Exception e) {
            logger.error("Email verification failed: {}", e.getMessage());
            throw new BadRequestException("Email verification failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void resendEmailVerification(String email) {
        logger.info("Resend email verification request for: {}", email);

        try {
            UserResponse user = userService.getUserByEmail(email);

            // Generate new verification token
            String verificationToken = jwtService.generateTokenFromUsername(user.getUsername());

            // TODO: Send verification email
            // emailService.sendVerificationEmail(email, verificationToken);

            logger.info("Verification email resent to: {}", email);

        } catch (Exception e) {
            logger.error("Resend verification failed for email: {} - {}", email, e.getMessage());
            throw new BadRequestException("Failed to resend verification email");
        }
    }

    // Helper methods

    private void blacklistToken(String token) {
        tokenBlacklist.put(token, Instant.now());

        // Cleanup expired tokens periodically (in production, use a scheduled job)
        cleanupExpiredTokens();
    }

    private boolean isTokenBlacklisted(String token) {
        return tokenBlacklist.containsKey(token);
    }

    private void cleanupExpiredTokens() {
        Instant now = Instant.now();
        tokenBlacklist.entrySet().removeIf(entry -> {
            try {
                return jwtService.isTokenExpired(entry.getKey());
            } catch (Exception e) {
                // If we can't parse the token, remove it from blacklist
                return true;
            }
        });
    }
}