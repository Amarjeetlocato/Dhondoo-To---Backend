package com.whoami.billing.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.whoami.billing.domain.entity.PaymentStatus;
import com.whoami.billing.domain.entity.Refund;

public interface RefundRepository extends JpaRepository<Refund, UUID> {

    List<Refund> findByPaymentId(UUID paymentId);

    Optional<Refund> findByGatewayRefundId(String gatewayRefundId);

    Optional<Refund> findByPaymentIdAndStatus(
            UUID paymentId,
            PaymentStatus status
    );

    @Query("""
            SELECT COALESCE(SUM(r.amount), 0)
            FROM Refund r
            WHERE r.paymentId = :paymentId
            AND r.status = :status
            """)
    BigDecimal sumRefundAmountByPaymentIdAndStatus(
            @Param("paymentId") UUID paymentId,
            @Param("status") PaymentStatus status
    );
}