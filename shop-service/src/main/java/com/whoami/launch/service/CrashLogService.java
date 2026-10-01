package com.whoami.launch.service;

import com.whoami.launch.dto.CrashReportRequestDTO;

public interface CrashLogService {

    void saveCrashReport(CrashReportRequestDTO request);
}