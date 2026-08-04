package com.whoami.launch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AdminRegisterRequest {

    @NotBlank
    private String username;

    @Email
    private String email;

    @NotBlank
    private String password;
}