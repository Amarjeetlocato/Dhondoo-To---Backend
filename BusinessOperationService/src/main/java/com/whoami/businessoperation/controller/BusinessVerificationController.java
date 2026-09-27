package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.dto.request.ReviewVerificationRequest;
import com.whoami.businessoperation.dto.request.SubmitVerificationRequest;
import com.whoami.businessoperation.dto.response.BusinessVerificationResponse;
import com.whoami.businessoperation.service.BusinessVerificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-operations/verifications")
@RequiredArgsConstructor
public class BusinessVerificationController {

    private final BusinessVerificationService businessVerificationService;

    @PostMapping
    public ResponseEntity<BusinessVerificationResponse> createVerification(
            @RequestParam UUID businessId,
            @RequestParam UUID applicationId) {

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
            @PathVariable UUID businessId) {

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
            @PathVariable UUID businessId) {

        return ResponseEntity.ok(
                businessVerificationService
                        .getVerificationHistory(businessId)
        );
    }
}