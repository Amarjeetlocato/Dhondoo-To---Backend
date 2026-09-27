package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.service.BusinessAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-operations/audits")
@RequiredArgsConstructor
public class BusinessAuditController {

    private final BusinessAuditService businessAuditService;

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessAuditLog>> getBusinessAuditLogs(
            @PathVariable UUID businessId) {

        return ResponseEntity.ok(
                businessAuditService.getBusinessAuditLogs(businessId)
        );
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<BusinessAuditLog>> getApplicationAuditLogs(
            @PathVariable UUID applicationId) {

        return ResponseEntity.ok(
                businessAuditService.getApplicationAuditLogs(applicationId)
        );
    }
}