package com.whoami.launch.service;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    // =========================================
    // COMMON HTML EMAIL METHOD
    // =========================================

    private void sendHtmlEmail(
            String to,
            String subject,
            String htmlContent) {

        try {

            MimeMessage message =
                    mailSender.createMimeMessage();

            MimeMessageHelper helper =
                    new MimeMessageHelper(
                            message,
                            true,
                            "UTF-8"
                    );

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to send email : "
                            + e.getMessage(),
                    e
            );
        }
    }

    // =========================================
    // WELCOME EMAIL
    // Template : welcome.html
    // =========================================

    public void sendWelcomeEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/welcome",
                        context
                );

        sendHtmlEmail(
                email,
                "Welcome to Launch 🎉",
                html
        );
    }

    // =========================================
    // FORGOT PASSWORD
    // Template : forgot-password.html
    // =========================================

    public void sendForgotPasswordEmail(
            String email,
            String otp) {

        Context context = new Context();

        context.setVariable("email", email);
        context.setVariable("otp", otp);

        String html =
                templateEngine.process(
                        "emails/forgot-password",
                        context
                );

        sendHtmlEmail(
                email,
                "Password Reset Request",
                html
        );
    }

    // =========================================
    // PASSWORD CHANGED
    // Template : password-changed.html
    // =========================================

    public void sendPasswordChangedEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/password-changed",
                        context
                );

        sendHtmlEmail(
                email,
                "Password Changed Successfully",
                html
        );
    }

    // =========================================
    // PASSWORD UPDATED
    // Template : password-updated.html
    // =========================================

    public void sendPasswordUpdatedEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/password-updated",
                        context
                );

        sendHtmlEmail(
                email,
                "Password Updated Successfully",
                html
        );
    }
    
   

    // =========================================
    // EMAIL UPDATED
    // Template : email-updated.html
    // =========================================

    public void sendEmailUpdatedEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/email-updated",
                        context
                );

        sendHtmlEmail(
                email,
                "Email Address Updated",
                html
        );
    }

    // =========================================
    // ACCOUNT DELETED
    // Template : deleted-account.html
    // =========================================

    public void sendAccountDeletedEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/deleted-account",
                        context
                );

        sendHtmlEmail(
                email,
                "Account Deleted Successfully",
                html
        );
    }

    // =========================================
    // ACCOUNT RESTORED
    // Template : account-retrived.html
    // =========================================

    public void sendAccountRetrievedEmail(
            String email,
            String username) {

        Context context = new Context();

        context.setVariable("name", username);

        String html =
                templateEngine.process(
                        "emails/account-retrived",
                        context
                );

        sendHtmlEmail(
                email,
                "Account Restored Successfully",
                html
        );
    }

    // =========================================
    // NEW LOGIN ALERT
    // Template : new-login.html
    // =========================================

    public void sendNewLoginEmail(
            String email,
            String username,
            String device,
            String time) {

        Context context = new Context();

        context.setVariable("name", username);
        context.setVariable("device", device);
        context.setVariable("time", time);

        String html =
                templateEngine.process(
                        "emails/new-login",
                        context
                );

        sendHtmlEmail(
                email,
                "New Login Detected",
                html
        );
    }
}