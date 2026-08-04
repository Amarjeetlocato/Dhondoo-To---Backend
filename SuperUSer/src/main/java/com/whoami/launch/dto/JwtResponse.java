package com.whoami.launch.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse {

    private String accessToken;

    private String refreshToken;

    private String adminId;

    private String email;
}