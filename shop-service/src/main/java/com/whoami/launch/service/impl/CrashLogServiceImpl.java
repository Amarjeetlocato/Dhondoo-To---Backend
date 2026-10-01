package com.whoami.launch.service.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.CrashReportRequestDTO;
import com.whoami.launch.entity.AppCrashLog;
import com.whoami.launch.repository.AppCrashLogRepository;
import com.whoami.launch.service.CrashLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CrashLogServiceImpl implements CrashLogService {

    private final AppCrashLogRepository crashLogRepository;

    @Override
    public void saveCrashReport(CrashReportRequestDTO request) {

        AppCrashLog crashLog = new AppCrashLog();

        crashLog.setUserId(request.getUserId());
        crashLog.setAppVersion(request.getAppVersion());
        crashLog.setBuildNumber(request.getBuildNumber());
        crashLog.setPlatform(request.getPlatform());
        crashLog.setDeviceModel(request.getDeviceModel());
        crashLog.setManufacturer(request.getManufacturer());
        crashLog.setOsVersion(request.getOsVersion());
        crashLog.setErrorType(request.getErrorType());
        crashLog.setErrorMessage(request.getErrorMessage());
        crashLog.setStackTrace(request.getStackTrace());
        crashLog.setScreenName(request.getScreenName());
        crashLog.setNetworkType(request.getNetworkType());
        crashLog.setAppFlavor(request.getAppFlavor());
        crashLog.setDeviceId(request.getDeviceId());
        crashLog.setScreenResolution(request.getScreenResolution());
        crashLog.setAppState(request.getAppState());
        crashLog.setCountry(request.getCountry());
        crashLog.setCity(request.getCity());
        crashLog.setCreatedAt(LocalDateTime.now());

        crashLogRepository.save(crashLog);
    }
}