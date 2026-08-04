package com.whoami.launch.service;

public interface OtpService {
    
    String generateAndSendOtp(String email, String otp);
    
    boolean validateOtp(String email, String otp);
    
    void invalidateOtp(String email);
}
