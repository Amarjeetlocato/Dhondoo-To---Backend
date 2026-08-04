package com.whoami.launch.service.impl;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.whoami.launch.entity.User;
import com.whoami.launch.exception.InvalidOtpException;
import com.whoami.launch.exception.UserNotFoundException;
import com.whoami.launch.repository.UserRepository;
import com.whoami.launch.service.EmailService;
import com.whoami.launch.service.OtpService;
import com.whoami.launch.util.OtpGenerator;

@Service
public class OtpServiceImpl implements OtpService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private OtpGenerator otpGenerator;
    
    @Autowired
    private EmailService emailService;

    public String generateAndSendOtp(String email,String otp) {

       

        sendOtpEmail(email, otp);

        System.out.println("✅ OTP EMAIL METHOD EXECUTED");

        return otp;
    }
    @Override
    public boolean validateOtp(
            String email,
            String otp
    ) {

        Optional<User> optionalUser =
                userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {

            throw new UserNotFoundException(
                    "User not found with email: " + email
            );
        }

        User user = optionalUser.get();

        // INVALID OTP
        if (!otp.equals(user.getOtp())) {

            throw new InvalidOtpException(
                    "Invalid OTP provided"
            );
        }

        // OTP EXPIRED
        if (LocalDateTime.now()
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

    // ================= SEND EMAIL =================

    public void sendOtpEmail(String toEmail, String otp) {

        try {

        	emailService.sendForgotPasswordEmail(
        	        toEmail,
        	        otp
        	);


            System.out.println("✅ EMAIL SENT SUCCESSFULLY");
            System.out.println("📧 Sent To: " + toEmail);
            System.out.println("🔐 OTP: " + otp);

        } catch (Exception e) {

            System.out.println("❌ EMAIL FAILED");
            e.printStackTrace();
            throw new RuntimeException(
                    "Unable to send OTP email");
        }
    }
}