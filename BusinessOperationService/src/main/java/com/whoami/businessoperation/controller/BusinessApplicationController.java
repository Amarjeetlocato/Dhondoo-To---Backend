package com.whoami.businessoperation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.businessoperation.dto.request.CreateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.UpdateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.response.BusinessApplicationResponse;
import com.whoami.businessoperation.service.BusinessApplicationService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessApplicationService.getApplication(businessId)
        );
    }

    @GetMapping("/owner/{ownerUserId}")
    public ResponseEntity<List<BusinessApplicationResponse>>
    getApplicationsByOwner(
            @PathVariable String ownerUserId) {

        return ResponseEntity.ok(
                businessApplicationService
                        .getApplicationsByOwner(ownerUserId)
        );
    }

    @PutMapping("/{businessId}")
    public ResponseEntity<BusinessApplicationResponse> updateApplication(
            @PathVariable String businessId,
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
            @PathVariable String businessId,
            @RequestParam String performedBy) {

        return ResponseEntity.ok(
                businessApplicationService.approveApplication(
                        businessId,
                        performedBy
                )
        );
    }

    @PostMapping("/{businessId}/reject")
    public ResponseEntity<BusinessApplicationResponse> rejectApplication(
            @PathVariable String businessId,
            @RequestParam String performedBy,
            @RequestParam String reason) {

        return ResponseEntity.ok(
                businessApplicationService.rejectApplication(
                        businessId,
                        reason,
                        performedBy
                )
        );
    }
    
    @PostMapping("/{businessId}/reupload")
    public ResponseEntity<BusinessApplicationResponse>
    requestApplicationReupload(
            @PathVariable String businessId,
            @RequestParam String performedBy,
            @RequestParam(required = false) String reason) {

        return ResponseEntity.ok(
                businessApplicationService.requestApplicationReupload(
                        businessId,
                        reason,
                        performedBy
                )
        );
    }
}