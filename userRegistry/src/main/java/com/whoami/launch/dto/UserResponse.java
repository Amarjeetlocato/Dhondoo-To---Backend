package com.whoami.launch.dto;

import lombok.Data;

@Data
public class UserResponse {

    private String userId;
    private String fullName;
    private String email;
    private boolean verified;
}