package com.whoami.launch.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminLoginRequest {

	

	    @NotBlank(message = "Username or email is required")
	    private String usernameOrEmail;

	    @NotBlank(message = "Password is required")
	    private String password;

	    @NotBlank(message = "Authenticator code is required")
	    private String totpCode;
	
}