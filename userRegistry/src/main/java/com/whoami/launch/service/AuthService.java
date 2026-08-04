package com.whoami.launch.service;

import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.RegisterRequest;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.JwtResponse;

public interface AuthService {
    
    JwtResponse login(LoginRequest request);
    
    String register(RegisterRequest request);
    
    String verifyOtp(String email, String otp);
    
    
	
	String forgotPassword(String email);

	String verifyResetOtp(
	        String email,
	        String otp);

	String updatePassword(
	        String email,
	        String newPassword);
	public ApiResponse<Void> deleteUser(String email);
	ApiResponse<Void> changeEmail(
	        String currentEmail,
	        String newEmail);
	
	JwtResponse refreshToken(String refreshToken);
}
