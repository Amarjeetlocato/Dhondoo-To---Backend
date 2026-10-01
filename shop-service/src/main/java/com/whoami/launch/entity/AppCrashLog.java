package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.launch.enums.ErrorType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "app_crash_logs")
@Data
public class AppCrashLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "crash_log_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 35
    )
    private String crashLogId;

    private String userId;

    private String appVersion;

    private String buildNumber;

    private String platform;

    private String deviceModel;

    private String manufacturer;

    private String osVersion;

    @Enumerated(EnumType.STRING)
    private ErrorType errorType;

    @Column(length = 10000)
    private String errorMessage;

    @Column(length = 500000)
    private String stackTrace;

    private String screenName;

    private String networkType;

    private String appFlavor;

    private String deviceId;

    private String screenResolution;

    private String appState;

    private String country;

    private String city;

    private LocalDateTime createdAt;

    @PrePersist
    protected void prePersist() {

        if (crashLogId == null) {
            crashLogId = "CRASH_LOG_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}