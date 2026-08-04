package com.whoami.launch.dto;

import lombok.Data;

@Data
public class TotpVerifyRequest {

    private String email;

    private String totpCode;
}