package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.dto.request.CreateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.UpdateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.response.BusinessApplicationResponse;
import com.whoami.businessoperation.service.BusinessApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-operations/applications")
@RequiredArgsConstructor
public class BusinessApplicationController {

    private final BusinessApplicationService businessApplicationService;

    @PostMapping
    public ResponseEntity<BusinessApplicationResponse> createApplication(
            @Valid @RequestBody CreateBusinessApplicationRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        businessApplicationService.createApplication(request)
                );
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<BusinessApplicationResponse> getApplication(
            @PathVariable UUID businessId) {

        return ResponseEntity.ok(
                businessApplicationService.getApplication(businessId)
        );
    }

    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<BusinessApplicationResponse>>
    getApplicationsByOwner(
            @PathVariable UUID ownerUserId) {

        return ResponseEntity.ok(
                businessApplicationService
                        .getApplicationsByOwner(ownerUserId)
        );
    }

    @PutMapping("/{businessId}")
    public ResponseEntity<BusinessApplicationResponse> updateApplication(
            @PathVariable UUID businessId,
            @Valid @RequestBody UpdateBusinessApplicationRequest request) {

        return ResponseEntity.ok(
                businessApplicationService.updateApplication(
                        businessId,
                        request
                )
        );
    }

    @PostMapping("/submit")
    public ResponseEntity<BusinessApplicationResponse> submitApplication(
            @Valid @RequestBody SubmitBusinessApplicationRequest request) {

        return ResponseEntity.ok(
                businessApplicationService.submitApplication(request)
        );
    }

    @PostMapping("/{businessId}/approve")
    public ResponseEntity<BusinessApplicationResponse> approveApplication(
            @PathVariable UUID businessId,
            @RequestParam UUID performedBy) {

        return ResponseEntity.ok(
                businessApplicationService.approveApplication(
                        businessId,
                        performedBy
                )
        );
    }

    @PostMapping("/{businessId}/reject")
    public ResponseEntity<BusinessApplicationResponse> rejectApplication(
            @PathVariable UUID businessId,
            @RequestParam UUID performedBy,
            @RequestParam String reason) {

        return ResponseEntity.ok(
                businessApplicationService.rejectApplication(
                        businessId,
                        reason,
                        performedBy
                )
        );
    }
}