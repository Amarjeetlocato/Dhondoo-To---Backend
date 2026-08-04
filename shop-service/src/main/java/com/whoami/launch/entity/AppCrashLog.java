package com.whoami.launch.entity;

import java.time.LocalDateTime;

import com.whoami.launch.enums.ErrorType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "app_crash_logs")
@Data
public class AppCrashLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}