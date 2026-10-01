package com.whoami.businessoperation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.businessoperation.domain.entity.BusinessAuditLog;
import com.whoami.businessoperation.service.BusinessAuditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/business-operations/audits")
@RequiredArgsConstructor
public class BusinessAuditController {

    private final BusinessAuditService businessAuditService;

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessAuditLog>> getBusinessAuditLogs(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessAuditService.getBusinessAuditLogs(businessId)
        );
    }

    @GetMapping("/application/{applicationId}")
    public ResponseEntity<List<BusinessAuditLog>> getApplicationAuditLogs(
            @PathVariable String applicationId) {

        return ResponseEntity.ok(
                businessAuditService.getApplicationAuditLogs(applicationId)
        );
    }
}