package com.whoami.launch.service.impl;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import com.whoami.launch.service.EmailService;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

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
                    "Failed to send email: "
                            + e.getMessage(),
                    e
            );
        }
    }

    @Override
    public void sendWelcomeEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendForgotPasswordEmail(
            String email,
            String otp) {

        Context context = new Context();

        context.setVariable(
                "email",
                email
        );

        context.setVariable(
                "otp",
                otp
        );

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

    @Override
    public void sendPasswordChangedEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendPasswordUpdatedEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendEmailUpdatedEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendAccountDeletedEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendAccountRetrievedEmail(
            String email,
            String fullName) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

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

    @Override
    public void sendNewLoginEmail(
            String email,
            String fullName,
            String device,
            String time) {

        Context context = new Context();

        context.setVariable(
                "name",
                fullName
        );

        context.setVariable(
                "device",
                device
        );

        context.setVariable(
                "time",
                time
        );

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