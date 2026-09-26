package com.whoami.billing.controller;

import com.whoami.billing.dto.request.CreateInvoiceRequest;
import com.whoami.billing.dto.response.InvoiceResponse;
import com.whoami.billing.service.InvoiceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/billing/invoices")
@RequiredArgsConstructor
public class InvoiceController {

    private final InvoiceService invoiceService;

    @PostMapping
    public InvoiceResponse createInvoice(
            @Valid @RequestBody CreateInvoiceRequest request) {
        return invoiceService.createInvoice(request);
    }

    @GetMapping("/{invoiceId}")
    public InvoiceResponse getById(
            @PathVariable UUID invoiceId) {
        return invoiceService.getById(invoiceId);
    }

    @GetMapping("/number/{invoiceNumber}")
    public InvoiceResponse getByInvoiceNumber(
            @PathVariable String invoiceNumber) {
        return invoiceService.getByInvoiceNumber(invoiceNumber);
    }

    @GetMapping("/transaction/{transactionId}")
    public InvoiceResponse getByTransactionId(
            @PathVariable UUID transactionId) {
        return invoiceService.getByTransactionId(transactionId);
    }

    @GetMapping("/customer/{customerId}")
    public List<InvoiceResponse> getByCustomerId(
            @PathVariable String customerId) {
        return invoiceService.getByCustomerId(customerId);
    }

    @PutMapping("/{invoiceId}/cancel")
    public InvoiceResponse cancelInvoice(
            @PathVariable UUID invoiceId) {
        return invoiceService.cancelInvoice(invoiceId);
    }
}