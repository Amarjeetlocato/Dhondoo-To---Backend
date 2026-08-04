package com.whoami.launch.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank
    @Email
    private String email;

    @NotBlank
    private String newPassword;
}