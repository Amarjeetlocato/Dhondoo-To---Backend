package com.whoami.launch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.entities.Business;
import com.whoami.launch.repositories.BusinessRepository;

@RestController
@RequestMapping("/api/chat/businesses")
public class BusinessChatController {

    @Autowired
    private BusinessRepository businessRepository;

    @PostMapping
    public ResponseEntity<Business> syncBusinessProfile(@RequestBody Business businessData) {
        if (businessData.getBusinessId() == null || businessData.getBusinessId().isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        Business business = businessRepository.findByBusinessId(businessData.getBusinessId())
                .orElseGet(() -> {
                    Business b = new Business();
                    b.setBusinessId(businessData.getBusinessId());
                    return b;
                });

        if (businessData.getBusinessName() != null) {
            business.setBusinessName(businessData.getBusinessName());
        }
        if (businessData.getImageUrl() != null) {
            business.setImageUrl(businessData.getImageUrl());
        }
        if (businessData.getDescription() != null) {
            business.setDescription(businessData.getDescription());
        }

        Business saved = businessRepository.save(business);
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<Business> getBusinessProfile(@PathVariable String businessId) {
        return businessRepository.findByBusinessId(businessId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
