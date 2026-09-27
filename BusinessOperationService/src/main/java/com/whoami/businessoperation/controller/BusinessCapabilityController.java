package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import com.whoami.businessoperation.dto.request.UpdateBusinessCapabilityRequest;
import com.whoami.businessoperation.dto.response.BusinessCapabilityResponse;
import com.whoami.businessoperation.service.BusinessCapabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business-operations/capabilities")
@RequiredArgsConstructor
public class BusinessCapabilityController {

    private final BusinessCapabilityService businessCapabilityService;

    @PostMapping("/enable")
    public ResponseEntity<BusinessCapabilityResponse> enableCapability(
            @Valid @RequestBody UpdateBusinessCapabilityRequest request) {

        return ResponseEntity.ok(
                businessCapabilityService.enableCapability(request)
        );
    }

    @PostMapping("/disable")
    public ResponseEntity<BusinessCapabilityResponse> disableCapability(
            @Valid @RequestBody UpdateBusinessCapabilityRequest request) {

        return ResponseEntity.ok(
                businessCapabilityService.disableCapability(request)
        );
    }

    @PostMapping("/suspend")
    public ResponseEntity<BusinessCapabilityResponse> suspendCapability(
            @Valid @RequestBody UpdateBusinessCapabilityRequest request) {

        return ResponseEntity.ok(
                businessCapabilityService.suspendCapability(request)
        );
    }

    @GetMapping("/{businessId}/{capabilityType}")
    public ResponseEntity<BusinessCapabilityResponse> getCapability(
            @PathVariable UUID businessId,
            @PathVariable CapabilityType capabilityType) {

        return ResponseEntity.ok(
                businessCapabilityService.getCapability(
                        businessId,
                        capabilityType
                )
        );
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessCapabilityResponse>>
    getBusinessCapabilities(
            @PathVariable UUID businessId) {

        return ResponseEntity.ok(
                businessCapabilityService
                        .getBusinessCapabilities(businessId)
        );
    }
}