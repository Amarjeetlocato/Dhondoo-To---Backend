package com.whoami.launch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReportRequest;
import com.whoami.launch.dto.ReportResponse;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.service.ReportService;

import jakarta.validation.Valid;

@RestController
public class ReportController {

    @Autowired
    private ReportService reportService;

    @PostMapping("/api/users/{userId}/reports")
    public ResponseEntity<ApiResponse<ReportResponse>> createReport(
            @PathVariable String userId,
            @Valid @RequestBody ReportRequest request) {

        ReportResponse response =
                reportService.createReport(
                        userId,
                        request);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Report submitted successfully",
                        response));
    }

    @GetMapping("/internal-api/reports")
    public ResponseEntity<ApiResponse<PageResponse<ReportResponse>>> getReports(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageResponse<ReportResponse> response =
                reportService.getReports(
                        page,
                        size);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Reports fetched successfully",
                        response));
    }

    @PatchMapping("/internal-api/reports/{reportId}/status")
    public ResponseEntity<ApiResponse<ReportResponse>> updateStatus(
            @PathVariable Long reportId,
            @RequestParam ReportStatus status) {

        ReportResponse response =
                reportService.updateStatus(
                        reportId,
                        status);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Report status updated",
                        response));
    }
}