package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.*;
import com.whoami.launch.service.AdminService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/admin/auth")
public class AdminAuthController {

    private final AdminService adminService;

    public AdminAuthController(
            AdminService adminService
    ) {
        this.adminService = adminService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(
            @Valid @RequestBody AdminRegisterRequest request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        adminService.register(request),
                        null
                )
        );
    }

    @GetMapping("/setup-totp/{email}")
    public ResponseEntity<ApiResponse<String>> setupTotp(
            @PathVariable String email
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Scan QR URL",
                        adminService.generateQrCode(email)
                )
        );
    }

    @PostMapping("/verify-totp")
    public ResponseEntity<ApiResponse<String>> verifyTotp(
            @RequestParam String email,
            @RequestParam String code
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        adminService.verifyTotpSetup(
                                email,
                                code
                        ),
                        null
                )
        );
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponse>> login(
            @Valid @RequestBody AdminLoginRequest request
    ) {
    	
    	System.out.println("CONTROLLER LOGIN HIT");

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Login successful",
                        adminService.login(request)
                )
        );
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtResponse>> refresh(
            @RequestBody RefreshTokenRequest request
    ) {

        return ResponseEntity.ok(
                new ApiResponse<>(
                        true,
                        "Token refreshed",
                        adminService.refreshToken(
                                request.getRefreshToken()
                        )
                )
        );
    }
}