package com.whoami.launch.service;

public interface EmailService {

    void sendWelcomeEmail(
            String email,
            String username
    );

    void sendForgotPasswordEmail(
            String email,
            String otp
    );

    void sendPasswordChangedEmail(
            String email,
            String username
    );

    void sendPasswordUpdatedEmail(
            String email,
            String username
    );

    void sendEmailUpdatedEmail(
            String email,
            String username
    );

    void sendAccountDeletedEmail(
            String email,
            String username
    );

    void sendAccountRetrievedEmail(
            String email,
            String username
    );

    void sendNewLoginEmail(
            String email,
            String username,
            String device,
            String time
    );
}