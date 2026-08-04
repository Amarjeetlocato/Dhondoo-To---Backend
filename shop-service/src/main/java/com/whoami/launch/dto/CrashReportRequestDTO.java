package com.whoami.launch.dto;

import com.whoami.launch.enums.ErrorType;

import lombok.Data;

@Data
public class CrashReportRequestDTO {

    private String userId;

    private String appVersion;

    private String buildNumber;

    private String platform;

    private String deviceModel;

    private String manufacturer;

    private String osVersion;

    private ErrorType errorType;

    private String errorMessage;

    private String stackTrace;

    private String screenName;

    private String networkType;

    private String appFlavor;

    private String deviceId;

    private String screenResolution;

    private String appState;

    private String country;

    private String city;
}