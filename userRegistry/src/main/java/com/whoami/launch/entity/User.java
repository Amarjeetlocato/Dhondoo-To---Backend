package com.whoami.launch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false, updatable = false)
    private String userId;

    @Column(unique = true, nullable = false,length = 50)
    private String username;

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String email;

    @Column(nullable = false, length = 100)
    private String password;

    private boolean verified = false;

    @Column(length = 100)
    private String otp;

    private LocalDateTime otpExpiry;

    private boolean accountNonLocked = true;

    private int failedLoginAttempts = 0;

    private LocalDateTime lockTime;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
    	if (userId == null) {
            userId = "USER_" + java.util.UUID.randomUUID()
                    .toString()
                    .replace("-", "")
                    .substring(0, 12)
                    .toUpperCase();
        }
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    @Column(name = "reset_otp")
    private String resetOtp;

    @Column(name = "reset_otp_expiry")
    private LocalDateTime resetOtpExpiry;

    @Column(name = "reset_otp_verified")
    private Boolean resetOtpVerified = false;
    
    @Column(name = "is_deleted")
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
}