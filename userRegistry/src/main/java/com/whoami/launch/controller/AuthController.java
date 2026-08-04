package com.whoami.launch.controller;

import java.security.Principal;
import java.util.Optional;

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

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/{userId}")
    public Optional<User> getUserByUserId(
            @PathVariable String userId) {

        try {
			return UserService.getUserByUserId(userId);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
    }
    // ================= LOGIN =================
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody LoginRequest request
    ) {

    	System.out.println("sgdsdgsgsdgfsgsgsgf login ");
        logger.info("Login request received");

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
            @Valid @RequestBody RegisterRequest request
    ) {

        logger.info("////////////Register request received: email={}, fullName={}, phone={}", 
                request.getEmail(), request.getUsernme() );

       
        String response =
                authService.register(request);
        logger.info("////////////////"+response);

        logger.info("Registration successful for email: {}", request.getEmail());

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
            @Valid @RequestBody VerifyOtpRequest request
    ) {

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
    
    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(
            @RequestBody ForgotPasswordRequest request) {

        System.out.println("=== CONTROLLER HIT ===");

        String response =
                authService.forgotPassword(
                        request.getEmail());

        System.out.println("=== SERVICE COMPLETED ===");

        return ResponseEntity.ok(
                new ApiResponse<>(true, response, null));
    }
    
        
    @PostMapping("/verify-reset-otp")
    public ResponseEntity<ApiResponse<String>> verifyResetOtp(
            @RequestBody VerifyOtpRequest request
    ) {

        String response =
                authService.verifyResetOtp(
                        request.getEmail(),
                        request.getOtp());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null));
    }
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse<String>> updatePassword(
            @RequestBody UpdatePasswordRequest request
    ) {

        String response =
                authService.updatePassword(
                        request.getEmail(),
                        request.getNewPassword());

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        response,
                        null));
    }

    
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount(
            Principal principal) {

        ApiResponse<Void> response =
                authService.deleteUser(
                        principal.getName()
                );

        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/change-email")
    public ResponseEntity<ApiResponse<Void>> changeEmail(
            @RequestBody ChangeEmailRequest request,
            Principal principal) {

        ApiResponse<Void> response =
                authService.changeEmail(
                        principal.getName(),
                        request.getNewEmail()
                );

        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>>
    refreshToken(
            @RequestBody RefreshTokenRequest request) {

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