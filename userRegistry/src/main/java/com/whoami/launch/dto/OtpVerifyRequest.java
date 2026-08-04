package com.whoami.launch.dto;



import lombok.Data;

@Data
public class OtpVerifyRequest {
    private String usernameOrEmail;
    private String otp;
}
