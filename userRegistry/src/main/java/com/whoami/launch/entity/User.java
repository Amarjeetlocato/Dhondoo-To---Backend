package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    /**
     * Internal database primary key.
     * Not shared between microservices.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public/system identity used across microservices.
     * Example: USER_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String userId;

    /**
     * User's full name.
     */
    @Column(
            name = "full_name",
            nullable = false,
            length = 100
    )
    private String fullName;

    /**
     * Login identity.
     */
    @Column(
            unique = true,
            nullable = false,
            length = 150
    )
    private String email;

    /**
     * Encoded password.
     */
    @Column(
            nullable = false,
            length = 255
    )
    private String password;

    /**
     * User account/email verification status.
     *
     * This is NOT business verification.
     */
    @Column(nullable = false)
    private boolean verified = false;

    /**
     * Registration/login OTP.
     */
    @Column(length = 100)
    private String otp;

    private LocalDateTime otpExpiry;

    /**
     * Login account lock status.
     */
    @Column(nullable = false)
    private boolean accountNonLocked = true;

    @Column(nullable = false)
    private int failedLoginAttempts = 0;

    private LocalDateTime lockTime;

    /**
     * Password reset OTP.
     */
    @Column(name = "reset_otp", length = 100)
    private String resetOtp;

    private LocalDateTime resetOtpExpiry;

    @Column(nullable = false)
    private Boolean resetOtpVerified = false;

    /**
     * Soft delete.
     */
    @Column(name = "is_deleted", nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    /**
     * Audit timestamps.
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void prePersist() {

        if (userId == null) {
            userId = "USER_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
