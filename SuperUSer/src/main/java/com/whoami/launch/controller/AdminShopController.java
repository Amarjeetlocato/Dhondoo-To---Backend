// Updated AdminShopController - Business & Document Verification, Status & Payments
package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.locato.dto.PaymentEvent;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.BusinessDocumentDTO;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessVerificationRequestDTO;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.service.impl.AdminShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping({"/admin/shops", "/admin/businesses"})
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    @GetMapping
    public ResponseEntity<PageResponse<ShopResponseDTO>> getAllShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllShops(page, size));
    }

    @GetMapping("/list")
    public ResponseEntity<PageResponse<BusinessResponseDTO>> getAllBusinesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllBusinesses(page, size));
    }

    @GetMapping("/pending-verifications")
    public ResponseEntity<PageResponse<BusinessResponseDTO>> getPendingVerifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getPendingVerifications(page, size));
    }

    /**
     * Controls/Updates business operational status (OPEN, CLOSED, ACTIVE, SUSPENDED, BLOCKED).
     */
    @PostMapping("/{businessId}/status")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>> updateBusinessStatus(
            @PathVariable String businessId,
            @RequestParam String status
    ) {
        BusinessResponseDTO response = adminShopService.updateBusinessStatus(businessId, status);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business status updated to " + status, response)
        );
    }

    /**
     * Enables, disables or suspends a business capability (e.g. SUBSCRIPTION, PAYMENTS).
     */
    @PostMapping("/{businessId}/capability")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>> updateCapability(
            @PathVariable String businessId,
            @RequestParam String capabilityType,
            @RequestParam(defaultValue = "ENABLE") String action,
            @RequestParam(required = false) String adminId
    ) {
        BusinessResponseDTO response = adminShopService.updateCapability(businessId, capabilityType, action, adminId);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Capability " + capabilityType + " " + action + "D for business " + businessId, response)
        );
    }

    /**
     * Toggles featured business status.
     */
    @PostMapping("/{businessId}/feature")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>> toggleFeaturedStatus(
            @PathVariable String businessId,
            @RequestParam(defaultValue = "true") boolean isFeatured
    ) {
        BusinessResponseDTO response = adminShopService.toggleFeaturedStatus(businessId, isFeatured);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business featured status updated to " + isFeatured, response)
        );
    }

    /**
     * Toggles verified badge for a business.
     */
    @PostMapping("/{businessId}/verify-badge")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>> toggleVerifiedBadge(
            @PathVariable String businessId,
            @RequestParam(defaultValue = "true") boolean isVerified
    ) {
        BusinessResponseDTO response = adminShopService.toggleVerifiedBadge(businessId, isVerified);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business verified badge updated to " + isVerified, response)
        );
    }

    /**
     * Verifies/Approves/Rejects a Business / Shop and sends event & notification.
     */
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<BusinessResponseDTO>> verifyBusiness(
            @Valid @RequestBody BusinessVerificationRequestDTO request
    ) {
        BusinessResponseDTO response = adminShopService.verifyBusiness(request);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business verification processed successfully", response)
        );
    }

    /**
     * Approves/Rejects specific business documents (GST, PAN, License).
     */
    @PostMapping("/documents/verify")
    public ResponseEntity<ApiResponse<BusinessDocumentDTO>> verifyDocument(
            @RequestBody BusinessDocumentDTO docDTO
    ) {
        BusinessDocumentDTO response = adminShopService.verifyBusinessDocument(docDTO);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business document verification processed", response)
        );
    }

    /**
     * Publishes a Payment Event to Kafka for processing and notification.
     */
    @PostMapping("/payments/event")
    public ResponseEntity<ApiResponse<String>> processPaymentEvent(
            @RequestBody PaymentEvent paymentEvent
    ) {
        adminShopService.processPaymentEvent(paymentEvent);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Payment event published to Kafka successfully", paymentEvent.getPaymentId())
        );
    }

    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerProfileResponseDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllCustomers(page, size));
    }

    @GetMapping("/products/{shopid}")
    public ResponseEntity<PageResponse<ProductResponseDTO>> getAllproducts(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllproducts(shopid, page, size));
    }

    @GetMapping("/services/{shopid}")
    public ResponseEntity<PageResponse<ServiceResponseDTO>> getAllServices(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllservices(shopid, page, size));
    }

    @GetMapping("/reels/{shopid}")
    public ResponseEntity<PageResponse<ReelResponseDTO>> getAllreels(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getAllreels(shopid, page, size));
    }
}
