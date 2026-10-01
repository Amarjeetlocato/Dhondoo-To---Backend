package com.whoami.businessoperation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;
import com.whoami.businessoperation.service.BusinessVerificationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/business-operations/verifications")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService businessVerificationService;

    @PostMapping
    public ResponseEntity<BusinessVerificationResponse> createVerification(
            @RequestParam String businessId,
            @RequestParam String applicationId) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        businessVerificationService.createVerification(
                                businessId,
                                applicationId
                        )
                );
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<BusinessVerificationResponse> getVerification(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessVerificationService.getVerification(businessId)
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<BusinessVerificationResponse>
    submitVerification(
            @Valid @RequestBody SubmitVerificationRequest request) {

        return ResponseEntity.ok(
                businessVerificationService.submitVerification(request)
        );
    }

    @PostMapping("/review")
    public ResponseEntity<BusinessVerificationResponse>
    reviewVerification(
            @Valid @RequestBody ReviewVerificationRequest request) {

        return ResponseEntity.ok(
                businessVerificationService.reviewVerification(request)
        );
    }

    @GetMapping("/{businessId}/history")
    public ResponseEntity<List<?>> getVerificationHistory(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessVerificationService
                        .getVerificationHistory(businessId)
        );
    }
}