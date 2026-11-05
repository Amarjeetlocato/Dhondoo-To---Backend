package com.whoami.launch.service.impl;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.locato.dto.UserCreatedEvent;
import com.locato.constants.topics.KafkaTopics;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.JwtResponse;
import com.whoami.launch.dto.LoginRequest;
import com.whoami.launch.dto.RegisterRequest;
import com.whoami.launch.entity.User;
import com.whoami.launch.exception.InvalidOtpException;
import com.whoami.launch.exception.UserAlreadyExistsException;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.security.JwtHelper;
import com.whoami.launch.service.AuthService;
import com.whoami.launch.service.EmailService;
import com.whoami.launch.service.OtpService;
import com.whoami.launch.util.OtpGenerator;

import jakarta.transaction.Transactional;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger logger =
            LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpGenerator otpGenerator;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public JwtResponse login(LoginRequest request) {

        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid credentials, please register."
                        ));

        if (user.isDeleted()) {
            throw new BadCredentialsException(
                    "Account is deleted"
            );
        }

        if (!user.isVerified()) {
            throw new RuntimeException(
                    "Please verify your account first"
            );
        }

        doAuthenticate(
                request.getEmail(),
                request.getPassword()
        );

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

        try {
            emailService.sendNewLoginEmail(
                    user.getEmail(),
                    user.getFullName(),
                    "Chrome Windows",
                    LocalDateTime.now().toString()
            );
        } catch (Exception e) {
            logger.error(
                    "Failed to send login email",
                    e
            );
        }

        return JwtResponse.builder()
                .jwtToken(accessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }

    @Override
    @Transactional
    public String register(RegisterRequest request) {

        java.util.Optional<User> existingUserOpt = userRepository.findByEmail(request.getEmail());

        if (existingUserOpt.isPresent()) {
            User existingUser = existingUserOpt.get();
            if (existingUser.isVerified()) {
                throw new UserAlreadyExistsException(
                        "User already exists with email: " + request.getEmail()
                );
            }

            // Unverified user exists -> update details, reset OTP, and send fresh registration OTP
            existingUser.setFullName(request.getFullName());
            existingUser.setPassword(passwordEncoder.encode(request.getPassword()));

            String otp = otpGenerator.generateOTP();
            existingUser.setOtp(passwordEncoder.encode(otp));
            existingUser.setOtpExpiry(LocalDateTime.now().plusMinutes(5));
            existingUser.setOtpAttempts(0);
            existingUser.setOtpBlockedUntil(null);
            existingUser.setVerified(false);

            User savedUser = userRepository.save(existingUser);

            otpService.generateAndSendOtp(request.getEmail(), otp);

            logger.info(
                    "Re-sent registration OTP for unverified user: {}",
                    savedUser.getUserId()
            );

            return "OTP sent successfully";
        }

        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        user.setPassword(
                passwordEncoder.encode(
                        request.getPassword()
                )
        );

        String otp = otpGenerator.generateOTP();

        user.setOtp(
                passwordEncoder.encode(otp)
        );

        user.setOtpExpiry(
                LocalDateTime.now().plusMinutes(5)
        );

        user.setVerified(false);

        User savedUser = userRepository.save(user);

        otpService.generateAndSendOtp(
                request.getEmail(),
                otp
        );

        logger.info(
                "Registration OTP generated for user: {}",
                savedUser.getUserId()
        );

        return "OTP sent successfully";
    }

    @Override
    public String verifyOtp(
            String email,
            String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "Invalid request"
                        ));

        // Idempotent check: If account is already verified, return success immediately
        if (user.isVerified()) {
            return "Account verified successfully";
        }

        LocalDateTime now = LocalDateTime.now();

        // Check temporary OTP block
        if (user.getOtpBlockedUntil() != null &&
                now.isBefore(user.getOtpBlockedUntil())) {

            throw new InvalidOtpException(
                    "Too many invalid OTP attempts. Please try again later."
            );
        }

        // Check OTP existence / expiry
        if (user.getOtp() == null ||
                user.getOtpExpiry() == null ||
                now.isAfter(user.getOtpExpiry())) {

            throw new InvalidOtpException(
                    "OTP expired"
            );
        }

        boolean validOtp =
                passwordEncoder.matches(
                        otp,
                        user.getOtp()
                );

        // Invalid OTP
        if (!validOtp) {

            int attempts = user.getOtpAttempts() + 1;
            user.setOtpAttempts(attempts);

            // Block after 5 failed attempts
            if (attempts >= 5) {

                user.setOtpBlockedUntil(
                        now.plusMinutes(15)
                );

                // Invalidate current OTP
                user.setOtp(null);
                user.setOtpExpiry(null);

                userRepository.save(user);

                throw new InvalidOtpException(
                        "Too many invalid OTP attempts. "
                        + "Please request a new OTP after 15 minutes."
                );
            }

            userRepository.save(user);

            throw new InvalidOtpException(
                    "Invalid OTP. Attempts remaining: "
                            + (5 - attempts)
            );
        }

        // Successful verification
        user.setVerified(true);

        user.setOtp(null);
        user.setOtpExpiry(null);

        // Reset OTP security state
        user.setOtpAttempts(0);
        user.setOtpBlockedUntil(null);

        userRepository.save(user);

        emailService.sendWelcomeEmail(
                user.getEmail(),
                user.getFullName()
        );

        // Publish user, customer profile, and location creation events ONLY AFTER OTP verification succeeds!
        publishUserCreatedEvent(user);
        publishCustomerProfileCreatedEvent(user);
        publishLocationCreatedEvent(user);

        return "Account verified successfully";
    }
    private void publishUserCreatedEvent(User user) {
        java.util.concurrent.CompletableFuture.runAsync(() -> {
            try {
                UserCreatedEvent event = UserCreatedEvent.builder()
                        .userId(user.getUserId())
                        .name(user.getFullName())
                        .email(user.getEmail())
                        .createdAt(LocalDateTime.now())
                        .build();

                kafkaTemplate.send(
                        KafkaTopics.USER_EVENTS,
                        user.getUserId(),
                        event
                );
            } catch (Exception e) {
                logger.error("Failed to publish UserCreatedEvent for userId={}: {}", user.getUserId(), e.getMessage());
            }
        });
    }

    private void publishCustomerProfileCreatedEvent(User user) {
        // Handled via user registration pipeline
    }

    private void publishLocationCreatedEvent(User user) {
        // Handled via user registration pipeline
    }

    private void doAuthenticate(
            String email,
            String password) {

        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                        email,
                        password
                );

        try {

            authenticationManager.authenticate(
                    authentication
            );

        } catch (BadCredentialsException ex) {

            throw new BadCredentialsException(
                    "Invalid email or password"
            );
        }
    }

    @Override
    public String forgotPassword(String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        String otp =
                otpGenerator.generateOTP();

        user.setResetOtp(
                passwordEncoder.encode(otp)
        );

        user.setResetOtpExpiry(
                LocalDateTime.now().plusMinutes(10)
        );

        user.setResetOtpVerified(false);
        
        

        userRepository.save(user);
        
        System.out.println(
                "RESET OTP SAVED | email=" + email +
                " | hash=" + user.getResetOtp() +
                " | expiry=" + user.getResetOtpExpiry()
        );

        otpService.generateAndSendResetOtp(
                email,
                otp
        );

        return "Password reset OTP sent successfully";
    }

    @Override
    public String verifyResetOtp(
            String email,
            String otp) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        if (user.getResetOtpExpiry() == null ||
                LocalDateTime.now()
                        .isAfter(user.getResetOtpExpiry())) {

            throw new InvalidOtpException(
                    "OTP expired"
            );
        }
        System.out.println(
                "RESET OTP VERIFY | email=" + email +
                " | expiry=" + user.getResetOtpExpiry() +
                " | hash=" + user.getResetOtp()
        );
        boolean validOtp =
        		
                passwordEncoder.matches(
                        otp,
                        user.getResetOtp()
                );

        if (!validOtp) {
            throw new InvalidOtpException(
                    "Invalid OTP"
            );
        }

        user.setResetOtpVerified(true);
        userRepository.save(user);

        return "Reset OTP verified successfully";
    }
    
    @Override
    public String updatePassword(
            String email,
            String newPassword) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        if (!Boolean.TRUE.equals(
                user.getResetOtpVerified())) {

            throw new InvalidOtpException(
                    "Please verify OTP first"
            );
        }

        user.setPassword(
                passwordEncoder.encode(
                        newPassword
                )
        );

        user.setResetOtp(null);
        user.setResetOtpExpiry(null);
        user.setResetOtpVerified(false);

        userRepository.save(user);

        emailService.sendPasswordUpdatedEmail(
                email,
                user.getFullName()
        );

        return "Password updated successfully";
    }

    @Override
    public ApiResponse<Void> deleteUser(
            String email) {

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        user.setDeleted(true);
        user.setDeletedAt(
                LocalDateTime.now()
        );

        userRepository.save(user);

        emailService.sendAccountDeletedEmail(
                user.getEmail(),
                user.getFullName()
        );

        return new ApiResponse<>(
                true,
                "Account deleted successfully",
                null
        );
    }

    @Override
    public ApiResponse<Void> changeEmail(
            String currentEmail,
            String newEmail) {

        User user = userRepository
                .findByEmail(currentEmail)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        if (userRepository.existsByEmail(newEmail)) {
            throw new UserAlreadyExistsException(
                    "Email already exists"
            );
        }

        user.setEmail(newEmail);

        userRepository.save(user);

        emailService.sendEmailUpdatedEmail(
                newEmail,
                user.getFullName()
        );

        return new ApiResponse<>(
                true,
                "Email updated successfully",
                null
        );
    }

    @Override
    public JwtResponse refreshToken(
            String refreshToken) {

        if (jwtHelper.isTokenExpired(
                refreshToken)) {

            throw new RuntimeException(
                    "Refresh token expired"
            );
        }

        String email =
                jwtHelper.getEmailFromToken(
                        refreshToken
                );

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException(
                                "User not found"
                        ));

        String newAccessToken =
                jwtHelper.generateToken(
                        user.getUserId(),
                        user.getEmail()
                );

        return JwtResponse.builder()
                .jwtToken(newAccessToken)
                .refreshToken(refreshToken)
                .userId(user.getUserId())
                .email(user.getEmail())
                .build();
    }
}

