package com.whoami.launch.controller;

import java.security.Principal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ChangeEmailRequest;
import com.whoami.launch.dto.ForgotPasswordRequest;
import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.RefreshTokenRequest;
import com.whoami.launch.dto.RegisterRequest;
import com.whoami.launch.dto.UpdatePasswordRequest;
import com.whoami.launch.dto.VerifyOtpRequest;
import com.whoami.launch.entity.User;
import com.whoami.launch.service.AuthService;
import com.whoami.launch.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;
    private final UserService userService;

    public AuthController(
            AuthService authService,
            UserService userService) {

        this.authService = authService;
        this.userService = userService;
    }

    // ================= GET USER =================

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserByUserId(
            @PathVariable String userId) {

        return userService.getUserByUserId(userId)
                .map(user -> ResponseEntity.ok(
                        new ApiResponse<>(
                                true,
                                "User found",
                                user
                        )
                ))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(
                                new ApiResponse<>(
                                        false,
                                        "User not found",
                                        null
                                )
                        ));
    }

    // ================= LOGIN =================

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        logger.info("Login request received for email: {}",
                request.getEmail());

        JwtResponse jwtResponse =
                authService.login(request);

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        jwtResponse
                )
        );
    }

    // ================= REGISTER =================

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody RegisterRequest request) {

        logger.info(
                "Registration request received for email: {}",
                request.getEmail()
        );

        String response =
                authService.register(request);

        logger.info(
                "Registration process completed for email: {}",
                request.getEmail()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new ApiResponse<>(
                                true,
                                response,
                                null
                        )
                );
    }

    // ================= VERIFY OTP =================

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        String response =
                authService.verifyOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null
                )
        );
    }

    // ================= FORGOT PASSWORD =================

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {

        String response =
                authService.forgotPassword(
                        request.getEmail()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null
                )
        );
    }

    // ================= VERIFY RESET OTP =================

    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<String>> verifyResetOtp(
            @Valid @RequestBody VerifyOtpRequest request) {

        String response =
                authService.verifyResetOtp(
                        request.getEmail(),
                        request.getOtp()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null
                )
        );
    }

    // ================= UPDATE PASSWORD =================

    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @Valid @RequestBody UpdatePasswordRequest request) {

        String response =
                authService.updatePassword(
                        request.getEmail(),
                        request.getNewPassword()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null
                )
        );
    }

    // ================= DELETE ACCOUNT =================

    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
            Principal principal) {

        return ResponseEntity.ok(
                authService.deleteUser(
                        principal.getName()
                )
        );
    }

    // ================= CHANGE EMAIL =================

    @PutMapping("/change-email")
    public ResponseEntity<ApiResponse<Void>> changeEmail(
            @Valid @RequestBody ChangeEmailRequest request,
            Principal principal) {

        return ResponseEntity.ok(
                authService.changeEmail(
                        principal.getName(),
                        request.getNewEmail()
                )
        );
    }

    // ================= REFRESH TOKEN =================

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refreshToken(
            @Valid @RequestBody RefreshTokenRequest request) {

        JwtResponse response =
                authService.refreshToken(
                        request.getRefreshToken()
                );

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Token refreshed successfully",
                        response
                )
        );
    }
}