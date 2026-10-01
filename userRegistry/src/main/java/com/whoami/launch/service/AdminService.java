package com.whoami.launch.service;

import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.OtpVerifyRequest;

public interface AdminService {

    String initiateAdminLogin(LoginRequest request);

    JwtResponse verifyAdminOtp(OtpVerifyRequest request);
}