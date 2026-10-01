package com.whoami.launch.service;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.RegisterRequest;

public interface AuthService {

    JwtResponse login(LoginRequest request);

    String register(RegisterRequest request);

    String verifyOtp(String email, String otp);

    String forgotPassword(String email);

    String verifyResetOtp(
            String email,
            String otp
    );

    String updatePassword(
            String email,
            String newPassword
    );

    ApiResponse<Void> deleteUser(String email);

    ApiResponse<Void> changeEmail(
            String currentEmail,
            String newEmail
    );

    JwtResponse refreshToken(String refreshToken);
}