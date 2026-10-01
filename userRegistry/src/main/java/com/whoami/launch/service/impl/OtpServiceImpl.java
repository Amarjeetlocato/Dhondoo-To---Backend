package com.whoami.launch.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whoami.launch.entity.User;
import com.whoami.launch.exception.InvalidOtpException;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.service.EmailService;
import com.whoami.launch.service.OtpService;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Override
    public String generateAndSendOtp(String email, String otp) {

        emailService.sendForgotPasswordEmail(
                email,
                otp
        );

        return otp;
    }

    @Override
    public boolean validateOtp(
            String email,
            String otp) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(
                    "User not found with email: " + email
            );
        }

        User user = optionalUser.get();

        if (user.getOtp() == null ||
                !passwordEncoder.matches(
                        otp,
                        user.getOtp()
                )) {

            throw new InvalidOtpException(
                    "Invalid OTP provided"
            );
        }

        if (user.getOtpExpiry() == null ||
                LocalDateTime.now()
                        .isAfter(user.getOtpExpiry())) {

            throw new InvalidOtpException(
                    "OTP has expired"
            );
        }

        return true;
    }

    @Override
    public void invalidateOtp(String email) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {

            User user = optionalUser.get();

            user.setOtp(null);
            user.setOtpExpiry(null);

            userRepository.save(user);
        }
    }
}