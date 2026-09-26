package com.whoami.billing.service.impl;

import com.whoami.billing.domain.entity.BillingTransaction;
import com.whoami.billing.domain.entity.BillingTransactionStatus;
import com.whoami.billing.domain.entity.Invoice;
import com.whoami.billing.domain.entity.InvoiceStatus;
import com.whoami.billing.dto.request.CreateInvoiceRequest;
import com.whoami.billing.dto.response.InvoiceResponse;
import com.whoami.billing.repository.BillingTransactionRepository;
import com.whoami.billing.repository.InvoiceRepository;
import com.whoami.billing.service.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class InvoiceServiceImpl implements InvoiceService {

    private final InvoiceRepository invoiceRepository;

    private final BillingTransactionRepository billingTransactionRepository;

    @Override
    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {

        BillingTransaction transaction = billingTransactionRepository
                .findById(request.transactionId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Billing transaction not found: "
                                        + request.transactionId()
                        )
                );

        if (invoiceRepository
                .findByTransactionId(transaction.getId())
                .isPresent()) {

            throw new IllegalStateException(
                    "Invoice already exists for transaction: "
                            + transaction.getId()
            );
        }

        if (transaction.getStatus() != BillingTransactionStatus.PAID) {
            throw new IllegalStateException(
                    "Invoice can only be created for a PAID transaction. Current status: "
                            + transaction.getStatus()
            );
        }

        Invoice invoice = Invoice.builder()
                .transactionId(transaction.getId())
                .customerId(transaction.getCustomerId())
                .referenceType(transaction.getReferenceType())
                .referenceId(transaction.getReferenceId())
                .purpose(transaction.getPurpose())
                .amount(transaction.getAmount())
                .currency(transaction.getCurrency())
                .status(InvoiceStatus.ISSUED)
                .build();

        return mapToResponse(invoiceRepository.save(invoice));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getById(UUID invoiceId) {

        return mapToResponse(findInvoice(invoiceId));
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getByInvoiceNumber(String invoiceNumber) {

        Invoice invoice = invoiceRepository
                .findByInvoiceNumber(invoiceNumber)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found: " + invoiceNumber
                        )
                );

        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public InvoiceResponse getByTransactionId(UUID transactionId) {

        Invoice invoice = invoiceRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found for transaction: "
                                        + transactionId
                        )
                );

        return mapToResponse(invoice);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getByCustomerId(String customerId) {

        return invoiceRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public InvoiceResponse cancelInvoice(UUID invoiceId) {

        Invoice invoice = findInvoice(invoiceId);

        if (invoice.getStatus() != InvoiceStatus.ISSUED) {
            throw new IllegalStateException(
                    "Only an issued invoice can be cancelled. Current status: "
                            + invoice.getStatus()
            );
        }

        invoice.setStatus(InvoiceStatus.CANCELLED);

        return mapToResponse(
                invoiceRepository.save(invoice)
        );
    }

    @Override
    public InvoiceResponse markAsRefunded(UUID invoiceId) {

        Invoice invoice = findInvoice(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot mark a cancelled invoice as refunded"
            );
        }

        invoice.setStatus(InvoiceStatus.REFUNDED);

        return mapToResponse(
                invoiceRepository.save(invoice)
        );
    }

    @Override
    public InvoiceResponse markAsPartiallyRefunded(UUID invoiceId) {

        Invoice invoice = findInvoice(invoiceId);

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot mark a cancelled invoice as partially refunded"
            );
        }

        invoice.setStatus(InvoiceStatus.PARTIALLY_REFUNDED);

        return mapToResponse(
                invoiceRepository.save(invoice)
        );
    }

    // ---------------------------------------------------------
    // Refund integration methods
    // ---------------------------------------------------------

    @Override
    public InvoiceResponse markAsRefundedForTransaction(
            UUID transactionId) {

        Invoice invoice = invoiceRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found for transaction: "
                                        + transactionId
                        )
                );

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot mark a cancelled invoice as refunded"
            );
        }

        invoice.setStatus(InvoiceStatus.REFUNDED);

        return mapToResponse(
                invoiceRepository.save(invoice)
        );
    }

    @Override
    public InvoiceResponse markAsPartiallyRefundedForTransaction(
            UUID transactionId) {

        Invoice invoice = invoiceRepository
                .findByTransactionId(transactionId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found for transaction: "
                                        + transactionId
                        )
                );

        if (invoice.getStatus() == InvoiceStatus.CANCELLED) {
            throw new IllegalStateException(
                    "Cannot mark a cancelled invoice as partially refunded"
            );
        }

        invoice.setStatus(InvoiceStatus.PARTIALLY_REFUNDED);

        return mapToResponse(
                invoiceRepository.save(invoice)
        );
    }

    private Invoice findInvoice(UUID invoiceId) {

        return invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Invoice not found: " + invoiceId
                        )
                );
    }
    
    @Override
    @Transactional(readOnly = true)
    public boolean existsByTransactionId(UUID transactionId) {
        return invoiceRepository.existsByTransactionId(transactionId);
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {

        return new InvoiceResponse(
                invoice.getId(),
                invoice.getInvoiceNumber(),
                invoice.getTransactionId(),
                invoice.getCustomerId(),
                invoice.getReferenceType(),
                invoice.getReferenceId(),
                invoice.getPurpose(),
                invoice.getAmount(),
                invoice.getCurrency(),
                invoice.getStatus(),
                invoice.getIssuedAt(),
                invoice.getCreatedAt(),
                invoice.getUpdatedAt()
        );
    }
}