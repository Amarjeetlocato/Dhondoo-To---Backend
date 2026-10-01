package com.whoami.launch.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.service.CustomerProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer-profiles")
@RequiredArgsConstructor
public class CustomerProfileController {

    private final CustomerProfileService customerProfileService;

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CustomerProfileResponseDTO>>>
    getAllCustomerProfiles(Pageable pageable) {

        Page<CustomerProfileResponseDTO> profiles =
                customerProfileService
                        .getAllcustomers(pageable)
                        .getContent()
                        .stream()
                        .collect(
                                java.util.stream.Collectors.collectingAndThen(
                                        java.util.stream.Collectors.toList(),
                                        list -> new org.springframework.data.domain.PageImpl<>(
                                                list,
                                                pageable,
                                                list.size()
                                        )
                                )
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer profiles retrieved",
                        profiles
                )
        );
    }

    // ================= GET BY USER ID =================

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>>
    getCustomerProfileByUserId(
            @PathVariable String userId) {

        return customerProfileService
                .getCustomerProfileByUserId(userId)
                .map(profile ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Customer profile retrieved",
                                        customerProfileService.toResponseDTO(profile)
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(
                                        ApiResponse.error(
                                                "Customer profile not found"
                                        )
                                )
                );
    }

    // ================= GET BY EMAIL =================

    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>>
    getCustomerProfileByEmail(
            @PathVariable String email) {

        return customerProfileService
                .getCustomerProfileByEmail(email)
                .map(profile ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Customer profile retrieved",
                                        customerProfileService.toResponseDTO(profile)
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(
                                        ApiResponse.error(
                                                "Customer profile not found"
                                        )
                                )
                );
    }

    // ================= UPDATE =================

    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>>
    updateCustomerProfile(
            @PathVariable String customerId,
            @Valid @RequestBody CustomerProfile customerProfileDetails) {

        CustomerProfile updatedProfile =
                customerProfileService.updateCustomerProfile(
                        customerId,
                        customerProfileDetails
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer profile updated successfully",
                        customerProfileService.toResponseDTO(updatedProfile)
                )
        );
    }

    // ================= DELETE =================

    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponse<Void>>
    deleteCustomerProfile(
            @PathVariable String customerId) {

        customerProfileService.deleteCustomerProfile(customerId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Customer profile deleted successfully",
                        null
                )
        );
    }

    // ================= INTERNAL - BY USER ID =================

    @GetMapping("/internal/user/{userId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>>
    getInternalCustomerProfileByUserId(
            @PathVariable String userId) {

        return customerProfileService
                .getCustomerProfileByUserId(userId)
                .map(profile ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Customer profile retrieved",
                                        customerProfileService.toResponseDTO(profile)
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.ok(
                                ApiResponse.error(
                                        "Customer profile not found"
                                )
                        )
                );
    }

    // ================= INTERNAL - BY EMAIL =================

    @GetMapping("/internal/email/{email}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>>
    getInternalCustomerProfileByEmail(
            @PathVariable String email) {

        return customerProfileService
                .getCustomerProfileByEmail(email)
                .map(profile ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Customer profile retrieved",
                                        customerProfileService.toResponseDTO(profile)
                                )
                        )
                )
                .orElseGet(() ->
                        ResponseEntity.ok(
                                ApiResponse.error(
                                        "Customer profile not found"
                                ))
                        );
    }

    // ================= INTERNAL - EXISTS =================

    @GetMapping("/internal/exists/{userId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkCustomerProfileExists(
            @PathVariable String userId) {

        boolean exists =
                customerProfileService
                        .getCustomerProfileByUserId(userId)
                        .isPresent();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        exists
                )
        );
    }
}