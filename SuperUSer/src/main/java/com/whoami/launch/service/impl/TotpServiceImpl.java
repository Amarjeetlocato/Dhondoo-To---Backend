package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;

import com.whoami.launch.service.TotpService;

import dev.samstevens.totp.code.CodeVerifier;
import dev.samstevens.totp.code.DefaultCodeGenerator;
import dev.samstevens.totp.code.DefaultCodeVerifier;
import dev.samstevens.totp.code.HashingAlgorithm;
import dev.samstevens.totp.secret.DefaultSecretGenerator;
import dev.samstevens.totp.time.SystemTimeProvider;

@Service
public class TotpServiceImpl implements TotpService {

    @Override
    public String generateSecret() {

        return new DefaultSecretGenerator()
                .generate();
    }

    @Override
    public String generateQrCodeUrl(
            String email,
            String secret
    ) {

        return "otpauth://totp/SuperUser:"
                + email
                + "?secret="
                + secret
                + "&issuer=SuperUser";
    }

    @Override
    public boolean verifyCode(
            String secret,
            String code
    ) {

        CodeVerifier verifier =
                new DefaultCodeVerifier(
                        new DefaultCodeGenerator(
                                HashingAlgorithm.SHA1),
                        new SystemTimeProvider()
                );

        return verifier.isValidCode(
                secret,
                code
        );
    }
}