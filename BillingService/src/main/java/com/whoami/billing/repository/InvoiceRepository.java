package com.whoami.billing.repository;

import com.whoami.billing.domain.entity.Invoice;
import com.whoami.billing.domain.entity.InvoiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {

    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);

    Optional<Invoice> findByTransactionId(UUID transactionId);

    List<Invoice> findByCustomerId(String customerId);

    List<Invoice> findByCustomerIdAndStatus(
            String customerId,
            InvoiceStatus status
    );

    List<Invoice> findByStatus(InvoiceStatus status);

    boolean existsByTransactionId(UUID transactionId);
}