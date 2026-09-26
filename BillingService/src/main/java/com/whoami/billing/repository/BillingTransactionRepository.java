package com.whoami.billing.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.billing.domain.entity.BillingTransaction;

import java.util.Optional;
import java.util.UUID;

public interface BillingTransactionRepository extends JpaRepository<BillingTransaction, UUID> {

    Optional<BillingTransaction> findByTransactionId(String transactionId);

    Optional<BillingTransaction> findByIdempotencyKey(String idempotencyKey);
}