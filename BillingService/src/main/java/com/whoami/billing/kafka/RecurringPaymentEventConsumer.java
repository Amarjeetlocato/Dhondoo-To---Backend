package com.whoami.billing.kafka;

import com.locato.dto.billing.event.RecurringPaymentRequestedEvent;
import com.locato.topics.BillingTopics;
import com.whoami.billing.domain.entity.Payment;
import com.whoami.billing.domain.entity.PaymentGateway;
import com.whoami.billing.dto.request.CreatePaymentRequest;
import com.whoami.billing.dto.response.PaymentResponse;
import com.whoami.billing.repository.BillingTransactionRepository;
import com.whoami.billing.repository.PaymentRepository;
import com.whoami.billing.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringPaymentEventConsumer {

    private final BillingTransactionRepository billingTransactionRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    @KafkaListener(
            topics = BillingTopics.RECURRING_PAYMENT_REQUESTED,
            groupId = "${spring.kafka.consumer.group-id}-recurring-payment",
            containerFactory = "recurringPaymentKafkaListenerContainerFactory"
    )
    public void consumeRecurringPaymentRequested(
            RecurringPaymentRequestedEvent event) {

        log.info(
                "Recurring payment request received: " +
                        "eventId={}, subscriptionId={}, " +
                        "transactionId={}, customerId={}, " +
                        "amount={}, currency={}, gateway={}, purpose={}",
                event.getEventId(),
                event.getSubscriptionId(),
                event.getTransactionId(),
                event.getCustomerId(),
                event.getAmount(),
                event.getCurrency(),
                event.getGateway(),
                event.getPurpose()
        );

        var transaction =
                billingTransactionRepository
                        .findByTransactionId(event.getTransactionId())
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Billing transaction not found: "
                                                + event.getTransactionId()
                                )
                        );

        UUID transactionId = transaction.getId();

        /*
         * Kafka can deliver the same event more than once.
         * Do not create another Payment if one already exists.
         */
        Payment existingPayment =
                paymentRepository
                        .findFirstByTransactionId(transactionId)
                        .orElse(null);

        if (existingPayment != null) {

            log.info(
                    "Recurring payment already exists: " +
                            "transactionId={}, paymentId={}, status={}",
                    event.getTransactionId(),
                    existingPayment.getId(),
                    existingPayment.getStatus()
            );

            return;
        }

        PaymentGateway gateway;

        try {
            gateway = PaymentGateway.valueOf(
                    event.getGateway().toUpperCase()
            );
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Unsupported payment gateway: "
                            + event.getGateway(),
                    e
            );
        }

        CreatePaymentRequest request =
                CreatePaymentRequest.builder()
                        .transactionId(transactionId)
                        .gateway(gateway)
                        .amount(event.getAmount())
                        .currency(event.getCurrency())
                        .paymentMethod("RECURRING")
                        .build();

        PaymentResponse payment =
                paymentService.create(request);

        log.info(
                "Recurring payment created: " +
                        "transactionId={}, paymentId={}, status={}, gateway={}",
                event.getTransactionId(),
                payment.getId(),
                payment.getStatus(),
                payment.getGateway()
        );
    }
}