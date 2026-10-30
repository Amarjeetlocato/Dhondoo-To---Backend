package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.domain.entity.BusinessUpdateRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessUpdateRequest;
import com.whoami.businessoperation.service.BusinessUpdateRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/business-operations/update-requests")
@RequiredArgsConstructor
public class BusinessUpdateRequestController {

    private final BusinessUpdateRequestService updateRequestService;

    @PostMapping
    public ResponseEntity<BusinessUpdateRequest> submitRequest(
            @Valid @RequestBody SubmitBusinessUpdateRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(updateRequestService.submitRequest(request));
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessUpdateRequest>> getRequestsByBusiness(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                updateRequestService.getRequestsByBusiness(businessId)
        );
    }

    @PutMapping("/{requestId}/approve")
    public ResponseEntity<BusinessUpdateRequest> approveRequest(
            @PathVariable String requestId,
            @RequestParam(defaultValue = "ADMIN") String reviewedBy) {

        return ResponseEntity.ok(
                updateRequestService.approveRequest(requestId, reviewedBy)
        );
    }

    @PutMapping("/{requestId}/reject")
    public ResponseEntity<BusinessUpdateRequest> rejectRequest(
            @PathVariable String requestId,
            @RequestParam(required = false) String reason,
            @RequestParam(defaultValue = "ADMIN") String reviewedBy) {

        return ResponseEntity.ok(
                updateRequestService.rejectRequest(requestId, reason, reviewedBy)
        );
    }
}
