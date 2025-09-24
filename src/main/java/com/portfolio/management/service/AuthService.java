package com.portfolio.management.service;

import com.portfolio.management.dto.request.LoginRequest;
import com.portfolio.management.dto.request.UserRegistrationRequest;
import com.portfolio.management.dto.response.JwtAuthResponse;
import com.portfolio.management.dto.response.UserResponse;

/**
 * Authentication Service Interface
 */
public interface AuthService {

    JwtAuthResponse login(LoginRequest loginRequest);

    UserResponse register(UserRegistrationRequest registrationRequest);

    JwtAuthResponse refreshToken(String refreshToken);

    void logout(String token);

    void forgotPassword(String email);

    void resetPassword(String token, String newPassword);

    void changePassword(String oldPassword, String newPassword);

    void verifyEmail(String verificationToken);

    void resendEmailVerification(String email);
}