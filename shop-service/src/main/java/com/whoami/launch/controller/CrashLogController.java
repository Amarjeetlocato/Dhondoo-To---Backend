package com.whoami.launch.controller;

import com.whoami.launch.dto.CrashReportRequestDTO;
import com.whoami.launch.service.CrashLogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mobile")
@RequiredArgsConstructor
public class CrashLogController {

    private final CrashLogService crashLogService;

    @PostMapping("/crash-report")
    public ResponseEntity<String> reportCrash(
            @Valid @RequestBody CrashReportRequestDTO request) {

        crashLogService.saveCrashReport(request);

        return ResponseEntity.ok(
                "Crash report saved successfully"
        );
    }
}