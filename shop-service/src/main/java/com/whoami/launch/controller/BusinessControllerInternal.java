package com.whoami.launch.controller;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.Business;
import com.whoami.launch.service.BusinessService;
import com.whoami.launch.service.CustomerProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/businesses")
public class BusinessControllerInternal {

    private final BusinessService businessService;
    private final CustomerProfileService customerProfileService;

    @GetMapping
    public ResponseEntity<PageResponse<BusinessResponseDTO>> getAllBusinesses(
            Pageable pageable) {

        return ResponseEntity.ok(
                businessService.getAllBusinesses(pageable));
    }

    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerProfileResponseDTO>>
    getAllCustomers(Pageable pageable) {

        return ResponseEntity.ok(
                customerProfileService.getAllcustomers(pageable));
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<Business> getBusiness(
            @PathVariable String businessId) {

        return businessService.getBusinessById(businessId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }
}