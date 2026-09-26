package com.whoami.billing.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.whoami.billing.domain.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    List<Payment> findByTransactionId(UUID transactionId);

    Optional<Payment> findFirstByTransactionId(UUID transactionId);

    Optional<Payment> findByGatewayOrderId(String gatewayOrderId);

    Optional<Payment> findByGatewayPaymentId(String gatewayPaymentId);
}