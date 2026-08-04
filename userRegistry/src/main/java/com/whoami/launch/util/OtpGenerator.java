package com.whoami.launch.util;

import org.springframework.stereotype.Component;

@Component
public class OtpGenerator {

    public String generateOTP() {
        return String.valueOf((int)(Math.random() * 900000) + 100000);
    }
}
