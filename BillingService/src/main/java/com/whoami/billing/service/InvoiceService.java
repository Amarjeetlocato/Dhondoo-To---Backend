package com.whoami.billing.service;

import com.whoami.billing.dto.request.CreateInvoiceRequest;
import com.whoami.billing.dto.response.InvoiceResponse;

import java.util.List;
import java.util.UUID;

public interface InvoiceService {

    InvoiceResponse createInvoice(CreateInvoiceRequest request);

    InvoiceResponse getById(UUID invoiceId);

    InvoiceResponse getByInvoiceNumber(String invoiceNumber);

    InvoiceResponse getByTransactionId(UUID transactionId);

    List<InvoiceResponse> getByCustomerId(String customerId);

    InvoiceResponse cancelInvoice(UUID invoiceId);
    
    InvoiceResponse markAsRefunded(UUID invoiceId);

    InvoiceResponse markAsPartiallyRefunded(UUID invoiceId);
    
    InvoiceResponse markAsRefundedForTransaction(UUID transactionId);

    InvoiceResponse markAsPartiallyRefundedForTransaction(UUID transactionId);
    
    boolean existsByTransactionId(UUID transactionId);
}