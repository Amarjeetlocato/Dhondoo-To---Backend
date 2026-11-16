package com.whoami.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.billing.domain.entity.BillingTransaction;
import com.whoami.billing.domain.entity.BillingTransactionStatus;

import java.util.Optional;
import java.util.UUID;

public interface BillingTransactionRepository extends JpaRepository<BillingTransaction, UUID> {

    Optional<BillingTransaction> findByTransactionId(String transactionId);

    Optional<BillingTransaction> findByIdempotencyKey(String idempotencyKey);

    Optional<BillingTransaction> findFirstByReferenceTypeAndReferenceIdAndPurposeAndStatus(
            String referenceType,
            String referenceId,
            String purpose,
            BillingTransactionStatus status
    );

    Optional<BillingTransaction> findFirstByReferenceTypeAndReferenceIdAndStatus(
            String referenceType,
            String referenceId,
            BillingTransactionStatus status
    );
}