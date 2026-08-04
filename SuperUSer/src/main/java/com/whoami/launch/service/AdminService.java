package com.whoami.launch.service;

import com.whoami.launch.dto.AdminLoginRequest;
import com.whoami.launch.dto.AdminRegisterRequest;
import com.whoami.launch.dto.JwtResponse;

public interface AdminService {

    String register(AdminRegisterRequest request);

    String generateQrCode(String email);

    String verifyTotpSetup(
            String email,
            String code
    );

    JwtResponse login(
            AdminLoginRequest request
    );

    JwtResponse refreshToken(
            String refreshToken
    );
}