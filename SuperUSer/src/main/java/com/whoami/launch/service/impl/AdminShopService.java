package com.whoami.launch.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.locato.dto.PaymentEvent;
import com.whoami.launch.dto.BillingSummaryDTO;
import com.whoami.launch.dto.BusinessDocumentDTO;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessVerificationRequestDTO;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.RefundRequestDTO;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.enums.ShopStatus;
import com.whoami.launch.service.KafkaEventPublisherService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminShopService {

    private final KafkaEventPublisherService eventPublisherService;

    public PageResponse<ShopResponseDTO> getAllShops(int page, int size) {
        PageResponse<BusinessResponseDTO> businessPage = getAllBusinesses(page, size);
        List<ShopResponseDTO> shopList = new ArrayList<>();

        for (BusinessResponseDTO b : businessPage.getContent()) {
            ShopResponseDTO s = new ShopResponseDTO();
            s.setShopId(b.getShopId());
            s.setUserId(b.getUserId());
            s.setShopName(b.getShopName());
            s.setMobileNumber(b.getMobileNumber());
            s.setAddress(b.getAddress());
            s.setLatitude(b.getLatitude());
            s.setLongitude(b.getLongitude());
            s.setTotalProducts(b.getTotalProducts());
            s.setTotalReels(b.getTotalReels());
            s.setTotalServices(b.getTotalServices());
            s.setStatus("OPEN".equalsIgnoreCase(b.getStatus()) ? ShopStatus.OPEN : ShopStatus.CLOSED);
            shopList.add(s);
        }

        return new PageResponse<>(
                shopList,
                businessPage.getPageNumber(),
                businessPage.getPageSize(),
                businessPage.getTotalElements(),
                businessPage.getTotalPages(),
                businessPage.isLast()
        );
    }

    public PageResponse<BusinessResponseDTO> getAllBusinesses(int page, int size) {
        List<BusinessResponseDTO> list = new ArrayList<>();
        return new PageResponse<>(list, page, size, 0L, 0, true);
    }

    public PageResponse<BusinessResponseDTO> getPendingVerifications(int page, int size) {
        List<BusinessResponseDTO> pendingList = new ArrayList<>();
        return new PageResponse<>(pendingList, page, size, 0L, 0, true);
    }

    /**
     * Updates business / shop operational status (OPEN, CLOSED, ACTIVE, SUSPENDED, BLOCKED).
     */
    public BusinessResponseDTO updateBusinessStatus(String businessId, String status) {
        log.info("Updating businessId: {} to status: {}", businessId, status);

        eventPublisherService.publishBusinessVerifiedEvent(
                businessId,
                "ADMIN_SYSTEM",
                "Business_" + businessId,
                status,
                "ADMIN",
                null,
                "Status updated to " + status
        );

        return BusinessResponseDTO.builder()
                .businessId(businessId)
                .shopId(businessId)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Enables, disables or suspends business capability (e.g. SUBSCRIPTION, PAYMENTS).
     */
    public BusinessResponseDTO updateCapability(String businessId, String capabilityType, String action, String adminId) {
        log.info("Updating capability for businessId: {}, type: {}, action: {}", businessId, capabilityType, action);
        eventPublisherService.publishCapabilityEvent(businessId, capabilityType, action, adminId);
        return BusinessResponseDTO.builder()
                .businessId(businessId)
                .shopId(businessId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Toggles featured status for a business / shop.
     */
    public BusinessResponseDTO toggleFeaturedStatus(String businessId, boolean isFeatured) {
        log.info("Toggling featured status for businessId: {} -> {}", businessId, isFeatured);

        eventPublisherService.sendNotification(
                "USER_" + businessId,
                isFeatured ? "Featured Business ⭐" : "Featured Status Updated",
                isFeatured ? "Congratulations! Your business is now featured on Dhondoo To." : "Your featured status has been updated.",
                businessId,
                "SHOP",
                "SYSTEM",
                "/shop/account"
        );

        return BusinessResponseDTO.builder()
                .businessId(businessId)
                .shopId(businessId)
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Toggles verified badge for a business / shop.
     */
    public BusinessResponseDTO toggleVerifiedBadge(String businessId, boolean isVerified) {
        log.info("Toggling verified badge for businessId: {} -> {}", businessId, isVerified);

        String status = isVerified ? "APPROVED" : "PENDING";
        return verifyBusiness(BusinessVerificationRequestDTO.builder()
                .businessId(businessId)
                .verificationStatus(status)
                .notes("Verified badge toggled by admin")
                .build());
    }

    /**
     * Verifies/Approves/Rejects a Business / Shop and triggers Kafka event & Notification-Service!
     */
    public BusinessResponseDTO verifyBusiness(BusinessVerificationRequestDTO request) {
        String businessId = request.getEffectiveBusinessId();
        String status = request.getVerificationStatus().toUpperCase();

        log.info("Admin verifying businessId: {} with status: {}", businessId, status);

        eventPublisherService.publishBusinessVerifiedEvent(
                businessId,
                request.getVerifiedByAdminId() != null ? request.getVerifiedByAdminId() : "ADMIN_SYSTEM",
                "Business_" + businessId,
                status,
                request.getVerifiedByAdminId(),
                request.getRejectionReason(),
                request.getNotes()
        );

        return BusinessResponseDTO.builder()
                .businessId(businessId)
                .shopId(businessId)
                .verificationStatus(status)
                .rejectionReason(request.getRejectionReason())
                .status("APPROVED".equalsIgnoreCase(status) ? "OPEN" : "SUSPENDED")
                .createdAt(LocalDateTime.now())
                .build();
    }

    /**
     * Verifies specific business documents (GST, PAN, License).
     */
    public BusinessDocumentDTO verifyBusinessDocument(BusinessDocumentDTO docDTO) {
        log.info("Verifying documentId: {} for businessId: {} -> Status: {}",
                docDTO.getDocumentId(), docDTO.getBusinessId(), docDTO.getVerificationStatus());

        if (docDTO.getUserId() != null) {
            String title = "APPROVED".equalsIgnoreCase(docDTO.getVerificationStatus())
                    ? "Document Approved ✅"
                    : "Document Verification Issue ⚠️";
            String msg = "Your " + docDTO.getDocumentType() + " document status is now: " + docDTO.getVerificationStatus();

            eventPublisherService.sendNotification(
                    docDTO.getUserId(),
                    title,
                    msg,
                    docDTO.getBusinessId(),
                    "DOCUMENT",
                    "SYSTEM",
                    "/shop/account/documents"
            );
        }

        docDTO.setVerifiedAt(LocalDateTime.now());
        return docDTO;
    }

    /**
     * Processes a refund request and publishes a PaymentEvent to Kafka.
     */
    public PaymentEvent processRefund(RefundRequestDTO refundRequest) {
        log.info("Processing refund for paymentId: {}, amount: {}", refundRequest.getPaymentId(), refundRequest.getAmount());

        PaymentEvent event = PaymentEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .paymentId(refundRequest.getPaymentId())
                .orderId(refundRequest.getOrderId())
                .userId(refundRequest.getUserId())
                .businessId(refundRequest.getBusinessId())
                .shopId(refundRequest.getBusinessId())
                .amount(refundRequest.getAmount())
                .currency(refundRequest.getCurrency() != null ? refundRequest.getCurrency() : "INR")
                .status("REFUNDED")
                .paymentMethod(refundRequest.getRefundMethod() != null ? refundRequest.getRefundMethod() : "ORIGINAL_PAYMENT_METHOD")
                .paymentType("REFUND")
                .failureReason(refundRequest.getReason())
                .timestamp(LocalDateTime.now())
                .build();

        eventPublisherService.publishPaymentEvent(event);

        if (refundRequest.getUserId() != null) {
            eventPublisherService.sendNotification(
                    refundRequest.getUserId(),
                    "Refund Processed 💸",
                    "A refund of ₹" + refundRequest.getAmount() + " for order #" + (refundRequest.getOrderId() != null ? refundRequest.getOrderId() : refundRequest.getPaymentId()) + " has been processed.",
                    refundRequest.getPaymentId(),
                    "PAYMENT",
                    "TRANSACTION",
                    "/account/orders"
            );
        }

        return event;
    }

    public PageResponse<PaymentEvent> getPaymentTransactions(int page, int size) {
        return new PageResponse<>(new ArrayList<>(), page, size, 0L, 0, true);
    }

    public BillingSummaryDTO getBillingSummary() {
        return BillingSummaryDTO.builder()
                .totalRevenue(new BigDecimal("0.00"))
                .totalRefunds(new BigDecimal("0.00"))
                .successfulTransactions(0L)
                .failedTransactions(0L)
                .activeSubscriptions(0L)
                .totalOrders(0L)
                .build();
    }

    public void cancelBusinessSubscription(String businessId, String reason) {
        log.info("Cancelling subscription for businessId: {}. Reason: {}", businessId, reason);

        eventPublisherService.sendNotification(
                "USER_" + businessId,
                "Subscription Cancelled",
                "Your business subscription has been cancelled: " + reason,
                businessId,
                "SUBSCRIPTION",
                "SYSTEM",
                "/shop/account/billing"
        );
    }

    public void processPaymentEvent(PaymentEvent event) {
        eventPublisherService.publishPaymentEvent(event);
    }

    public PageResponse<CustomerProfileResponseDTO> getAllCustomers(int page, int size) {
        return new PageResponse<>(new ArrayList<>(), page, size, 0L, 0, true);
    }

    public PageResponse<ProductResponseDTO> getAllproducts(String shopid, int page, int size) {
        return new PageResponse<>(new ArrayList<>(), page, size, 0L, 0, true);
    }

    public PageResponse<ReelResponseDTO> getAllreels(String shopid, int page, int size) {
        return new PageResponse<>(new ArrayList<>(), page, size, 0L, 0, true);
    }

    public PageResponse<ServiceResponseDTO> getAllservices(String shopid, int page, int size) {
        return new PageResponse<>(new ArrayList<>(), page, size, 0L, 0, true);
    }
}
