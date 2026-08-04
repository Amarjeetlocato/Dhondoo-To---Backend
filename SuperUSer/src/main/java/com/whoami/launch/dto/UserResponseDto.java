package com.whoami.launch.dto;

import lombok.Data;

@Data
public class UserResponseDto {

    private String userId;
    private String username;
    private String email;
    private Boolean verified;
}