package com.whoami.launch.service;

public interface TotpService {

    String generateSecret();

    String generateQrCodeUrl(
            String email,
            String secret
    );

    boolean verifyCode(
            String secret,
            String code
    );
}