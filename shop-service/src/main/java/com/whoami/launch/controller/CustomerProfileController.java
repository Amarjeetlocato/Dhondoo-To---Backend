package com.whoami.launch.controller;

import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.CustomerProfileCreatedEvent;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.CustomerProfileSummaryDTO;
import com.whoami.launch.dto.UserProfileResponse;
import com.whoami.launch.service.CustomerProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class CustomerProfileController {
    
    @Autowired
    private CustomerProfileService customerProfileService;
    
    @Autowired
    private CustomerProfileRepository customerProfileRepository;
    
   
    
    
    @KafkaListener(topics = "customer-created-topic", groupId = "shop-group")
    public void createCustomer(CustomerProfileCreatedEvent request) {

        CustomerProfile profile = new CustomerProfile();

        profile.setUserId(request.getUserId());
        profile.setUsername(request.getUsername());
        profile.setEmail(request.getEmail());

        customerProfileService.createCustomerProfile(profile);
    }
    // Public API endpoints
    @GetMapping("/api/customer-profiles")
    public ResponseEntity<List<CustomerProfile>> getAllCustomerProfiles() {
        return ResponseEntity.ok(customerProfileService.getAllCustomerProfiles());
    }
    
    @GetMapping("/api/customer-profiles/{customerId}")
    public ResponseEntity<Optional<CustomerProfile>> getCustomerProfileById(@PathVariable String customerId) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileById(customerId);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/api/customer-profiles/user/{userId}")
    public ResponseEntity<Optional<CustomerProfile>> getCustomerProfileByUserId(@PathVariable String userId) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByUserId(userId);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/api/customer-profiles/search/username/{username}")
    public ResponseEntity<Optional<CustomerProfile>> getCustomerProfileByUsername(@PathVariable String username) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByUsername(username);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/api/customer-profiles/search/email/{email}")
    public ResponseEntity<Optional<CustomerProfile>> getCustomerProfileByEmail(@PathVariable String email) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByEmail(email);
        if (profile.isPresent()) {
            return ResponseEntity.ok(profile);
        }
        return ResponseEntity.notFound().build();
    }
    
    
   
    @PutMapping("/api/customer-profiles/{customerId}")
    public ResponseEntity<CustomerProfile> updateCustomerProfile(@PathVariable String customerId, @RequestBody CustomerProfile customerProfileDetails) {
        CustomerProfile updatedProfile = customerProfileService.updateCustomerProfile(customerId, customerProfileDetails);
        if (updatedProfile != null) {
            return ResponseEntity.ok(updatedProfile);
        }
        return ResponseEntity.notFound().build();
    }
    
    @DeleteMapping("/api/customer-profiles/{customerId}")
    public ResponseEntity<Void> deleteCustomerProfile(@PathVariable String customerId) {
        customerProfileService.deleteCustomerProfile(customerId);
        return ResponseEntity.noContent().build();
    }
    
    // Internal API endpoints for Feign clients
    @GetMapping("/internal-api/customer-profiles/user/{userId}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>> getInternalCustomerProfileByUserId(@PathVariable String userId) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByUserId(userId);
        if (profile.isPresent()) {
            CustomerProfileResponseDTO dto = customerProfileService.toResponseDTO(profile.get());
            return ResponseEntity.ok(ApiResponse.success("Customer profile retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Customer profile not found"));
    }
    
    @GetMapping("/internal-api/customer-profiles/username/{username}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>> getInternalCustomerProfileByUsername(@PathVariable String username) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByUsername(username);
        if (profile.isPresent()) {
            CustomerProfileResponseDTO dto = customerProfileService.toResponseDTO(profile.get());
            return ResponseEntity.ok(ApiResponse.success("Customer profile retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Customer profile not found"));
    }
    
    @GetMapping("/internal-api/customer-profiles/email/{email}")
    public ResponseEntity<ApiResponse<CustomerProfileResponseDTO>> getInternalCustomerProfileByEmail(@PathVariable String email) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByEmail(email);
        if (profile.isPresent()) {
            CustomerProfileResponseDTO dto = customerProfileService.toResponseDTO(profile.get());
            return ResponseEntity.ok(ApiResponse.success("Customer profile retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Customer profile not found"));
    }
    
    @GetMapping("/internal-api/customer-profiles/exists/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkCustomerProfileExists(@PathVariable String userId) {
        Optional<CustomerProfile> profile = customerProfileService.getCustomerProfileByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", profile.isPresent()));
    }
    
    @GetMapping("/{userId}/profile")
    public UserProfileResponse getUserProfile(
            @PathVariable String userId) {

        CustomerProfile profile =
                customerProfileRepository
                        .findByUserId(userId)
                        .orElseThrow();

        return new UserProfileResponse(
                profile.getUserId(),
                profile.getUsername(),
                profile.getLogoUrl()
        );
    }
    
    
}

