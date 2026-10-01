package com.whoami.launch.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.OtpVerifyRequest;
import com.whoami.launch.entity.User;
import com.whoami.launch.exception.InvalidOtpException;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.security.JwtHelper;
import com.whoami.launch.service.AdminService;
import com.whoami.launch.service.EmailService;
import com.whoami.launch.service.OtpService;
import com.whoami.launch.util.OtpGenerator;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpGenerator otpGenerator;

    @Override
    public String initiateAdminLogin(
            LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid credentials"
                        ));

        if (user.isDeleted()) {
            throw new BadCredentialsException(
                    "Account is deleted"
            );
        }

        if (!user.isVerified()) {
            throw new BadCredentialsException(
                    "User not verified"
            );
        }

        doAuthenticate(
                request.getEmail(),
                request.getPassword()
        );

        String otp =
                otpGenerator.generateOTP();

        String hashedOtp =
                passwordEncoder.encode(otp);

        user.setOtp(hashedOtp);
        user.setOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        userRepository.save(user);

        otpService.generateAndSendOtp(
                user.getEmail(),
                otp
        );

        return "OTP sent to admin email";
    }

    @Override
    public JwtResponse verifyAdminOtp(
            OtpVerifyRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        if (user.isDeleted()) {
            throw new UserNotFoundException(
                    "User not found"
            );
        }

        if (user.getOtpExpiry() == null ||
                LocalDateTime.now()
                        .isAfter(user.getOtpExpiry())) {

            throw new InvalidOtpException(
                    "OTP expired"
            );
        }

        boolean validOtp =
                passwordEncoder.matches(
                        request.getOtp(),
                        user.getOtp()
                );

        if (!validOtp) {
            throw new InvalidOtpException(
                    "Invalid OTP"
            );
        }

        user.setOtp(null);
        user.setOtpExpiry(null);

        userRepository.save(user);

        String accessToken =
                jwtHelper.generateToken(
                        user.getUserId(),
                        user.getEmail()
                );

        String refreshToken =
                jwtHelper.generateRefreshToken(
                        user.getUserId(),
                        user.getEmail()
                );

        emailService.sendNewLoginEmail(
                user.getEmail(),
                user.getFullName(),
                "ADMIN PANEL LOGIN",
                LocalDateTime.now().toString()
        );

        return JwtResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }

    private void doAuthenticate(
            String email,
            String password) {

        try {

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            email,
                            password
                    )
            );

        } catch (BadCredentialsException ex) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }
    }
}