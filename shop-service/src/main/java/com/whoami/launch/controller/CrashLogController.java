package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.CrashReportRequestDTO;
import com.whoami.launch.service.CrashLogService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class CrashLogController {

    private final CrashLogService crashLogService;

    @PostMapping("/crash-report")
    public ResponseEntity<String> reportCrash(
            @RequestBody CrashReportRequestDTO request) {

        crashLogService.saveCrashReport(request);

        return ResponseEntity.ok(
                "Crash report saved successfully");
    }
}