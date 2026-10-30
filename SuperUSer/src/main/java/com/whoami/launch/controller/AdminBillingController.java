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
import com.whoami.launch.dto.BillingSummaryDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.RefundRequestDTO;
import com.whoami.launch.service.impl.AdminShopService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/billing")
@RequiredArgsConstructor
public class AdminBillingController {

    private final AdminShopService adminShopService;

    /**
     * Processes a refund request for a user/business transaction.
     */
    @PostMapping("/refund")
    public ResponseEntity<ApiResponse<PaymentEvent>> processRefund(
            @Valid @RequestBody RefundRequestDTO refundRequest
    ) {
        PaymentEvent refundEvent = adminShopService.processRefund(refundRequest);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Refund processed successfully and event published", refundEvent)
        );
    }

    /**
     * Retrieves all payment transactions / audit records.
     */
    @GetMapping("/payments")
    public ResponseEntity<PageResponse<PaymentEvent>> getPayments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminShopService.getPaymentTransactions(page, size));
    }

    /**
     * Retrieves system billing & revenue summary.
     */
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<BillingSummaryDTO>> getBillingSummary() {
        BillingSummaryDTO summary = adminShopService.getBillingSummary();
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Billing summary fetched successfully", summary)
        );
    }

    /**
     * Cancels or modifies a business subscription.
     */
    @PostMapping("/subscriptions/cancel/{businessId}")
    public ResponseEntity<ApiResponse<String>> cancelSubscription(
            @PathVariable String businessId,
            @RequestParam(defaultValue = "Cancelled by admin") String reason
    ) {
        adminShopService.cancelBusinessSubscription(businessId, reason);
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Business subscription cancelled successfully", businessId)
        );
    }
}
