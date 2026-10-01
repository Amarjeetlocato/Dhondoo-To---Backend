package com.whoami.businessoperation.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.businessoperation.domain.enums.CapabilityType;
import com.whoami.businessoperation.dto.request.UpdateBusinessCapabilityRequest;
import com.whoami.businessoperation.dto.response.BusinessCapabilityResponse;
import com.whoami.businessoperation.service.BusinessCapabilityService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

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
            @PathVariable String businessId,
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
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessCapabilityService
                        .getBusinessCapabilities(businessId)
        );
    }
}