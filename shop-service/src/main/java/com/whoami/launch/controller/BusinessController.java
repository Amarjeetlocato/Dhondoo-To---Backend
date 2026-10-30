package com.whoami.launch.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessStatusUpdateRequest;
import com.whoami.launch.dto.BusinessSummaryDTO;
import com.whoami.launch.dto.FollowBusinessResponse;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.entity.Business;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.service.BusinessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping({"/api/businesses", "/api/shops"})
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping
    public ResponseEntity<List<Business>> getAllBusinesses() {

        return ResponseEntity.ok(
                businessService.getAllBusinesses());
    }

    @GetMapping("/{businessId}")
    public ResponseEntity<Business> getBusinessById(
            @PathVariable String businessId) {

        Optional<Business> business =
                businessService.getBusinessById(businessId);

        return business
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/search/name/{businessName}")
    public ResponseEntity<Business> getBusinessByName(
            @PathVariable String businessName) {

        Optional<Business> business =
                businessService.getBusinessByName(
                        businessName);

        return business
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Business>> getBusinessesByUserId(
            @PathVariable String userId) {

        return ResponseEntity.ok(
                businessService.getBusinessesByUserId(userId));
    }

    @GetMapping("/search/query")
    public ResponseEntity<List<Business>> searchBusinesses(
            @RequestParam String query) {

        return ResponseEntity.ok(
                businessService.searchBusinesses(query));
    }

    @PostMapping
    public ResponseEntity<Business> createBusiness(
            @RequestBody Business business) {

        Business createdBusiness =
                businessService.createBusiness(business);

        return ResponseEntity.ok(createdBusiness);
    }

    @PatchMapping("/{businessId}")
    public ResponseEntity<Business> updateBusiness(
            @PathVariable String businessId,
            @RequestBody Business businessDetails) {

        Business updatedBusiness =
                businessService.updateBusiness(
                        businessId,
                        businessDetails);

        if (updatedBusiness != null) {
            return ResponseEntity.ok(updatedBusiness);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{businessId}")
    public ResponseEntity<Void> deleteBusiness(
            @PathVariable String businessId) {

        businessService.deleteBusiness(businessId);

        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{businessId}/status")
    public ResponseEntity<BusinessResponseDTO> updateBusinessStatus(
            @PathVariable String businessId,
            @RequestBody BusinessStatusUpdateRequest request) {

        Business business =
                businessService.updateBusinessStatus(
                        businessId,
                        request.getBusinessStatus());

        BusinessResponseDTO dto =
                businessService.toResponseDTO(business);

        return ResponseEntity.ok(dto);
    }

    @PostMapping("/{businessId}/follow")
    public ResponseEntity<ApiResponse<FollowBusinessResponse>> followBusiness(
            @PathVariable String businessId,
            @RequestParam String userId) {

        FollowBusinessResponse response =
                businessService.followBusiness(
                        businessId,
                        userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business followed successfully",
                        response));
    }

    @DeleteMapping("/{businessId}/follow")
    public ResponseEntity<ApiResponse<FollowBusinessResponse>> unfollowBusiness(
            @PathVariable String businessId,
            @RequestParam String userId) {

        FollowBusinessResponse response =
                businessService.unfollowBusiness(
                        businessId,
                        userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business unfollowed successfully",
                        response));
    }

    @GetMapping("/{businessId}/follow")
    public ResponseEntity<ApiResponse<FollowBusinessResponse>> getFollowStatus(
            @PathVariable String businessId,
            @RequestParam String userId) {

        FollowBusinessResponse response =
                businessService.getFollowStatus(
                        businessId,
                        userId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Follow status fetched",
                        response));
    }

    @PostMapping("/{businessId}/change-request")
    public ResponseEntity<ApiResponse<String>> requestBusinessDetailsChange(
            @PathVariable String businessId,
            @RequestBody Business changeDetails) {

        businessService.requestBusinessDetailsChange(
                businessId,
                changeDetails);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business details change request submitted successfully and is pending approval",
                        businessId));
    }

    @GetMapping({"/{shopId}/reviews", "/{shopId}/reviews/list"})
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> getShopReviews(
            @PathVariable String shopId) {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Shop reviews fetched",
                        new java.util.ArrayList<>()
                )
        );
    }

    @GetMapping("/internal-api/businesses/{businessId}")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>>
    getInternalBusinessById(
            @PathVariable String businessId) {

        Business business =
                businessService.getBusinessById(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found"));

        BusinessResponseDTO dto =
                businessService.toResponseDTO(business);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Business retrieved",
                        dto));
    }

    @GetMapping("/internal-api/businesses/user/{userId}")
    public ResponseEntity<ApiResponse<List<BusinessSummaryDTO>>>
    getInternalBusinessesByUserId(
            @PathVariable String userId) {

        List<Business> businesses =
                businessService.getBusinessesByUserId(userId);

        List<BusinessSummaryDTO> dtos =
                businesses.stream()
                        .map(businessService::toSummaryDTO)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Businesses retrieved",
                        dtos));
    }

    @GetMapping("/internal-api/businesses/exists/{businessId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkBusinessExists(
            @PathVariable String businessId) {

        boolean exists =
                businessService.getBusinessById(businessId)
                        .isPresent();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        exists));
    }

    @GetMapping("/internal-api/businesses/exists-by-user/{userId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkBusinessExistsByUserId(
            @PathVariable String userId) {

        boolean exists =
                !businessService
                        .getBusinessesByUserId(userId)
                        .isEmpty();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        exists));
    }
    
    
    		@PostMapping("/{businessId}/verification/request")
    		public ResponseEntity<ApiResponse<Void>> requestVerification(
    		        @PathVariable String businessId) {

    		    businessService.requestVerification(businessId);

    		    return ResponseEntity.ok(
    		            ApiResponse.success(
    		                    "Business verification request submitted",
    		                    null
    		            )
    		    );
    		}
    		

}