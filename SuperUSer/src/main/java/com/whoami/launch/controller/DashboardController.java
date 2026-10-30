package com.whoami.launch.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.BillingSummaryDTO;
import com.whoami.launch.service.impl.AdminShopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final AdminShopService adminShopService;

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(
                Map.of(
                        "status", "UP",
                        "service", "SUPER_USER"
                )
        );
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<BillingSummaryDTO>> getDashboardStats() {
        BillingSummaryDTO summary = adminShopService.getBillingSummary();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Dashboard stats fetched successfully", summary)
        );
    }
}
