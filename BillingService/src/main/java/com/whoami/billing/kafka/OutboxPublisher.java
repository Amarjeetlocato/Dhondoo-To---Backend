package com.whoami.billing.kafka;

import com.whoami.billing.domain.entity.OutboxEvent;
import com.whoami.billing.domain.entity.OutboxStatus;
import com.whoami.billing.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OutboxPublisher {

    private final OutboxEventRepository repository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @Scheduled(fixedDelay = 5000)
    @Transactional
    public void publishPendingEvents() {

        List<OutboxEvent> events =
                repository.findTop100ByStatusOrderByCreatedAtAsc(
                        OutboxStatus.PENDING
                );

        for (OutboxEvent event : events) {
            publishEvent(event);
        }
    }

    private void publishEvent(OutboxEvent event) {

        try {

            kafkaTemplate.send(
                    event.getTopic(),
                    event.getAggregateId(),
                    event.getPayload()
            ).get();

            event.setStatus(OutboxStatus.PUBLISHED);
            event.setPublishedAt(LocalDateTime.now());
            event.setLastError(null);

            repository.save(event);

            log.info(
                    "Outbox event published: id={}, eventType={}, topic={}",
                    event.getId(),
                    event.getEventType(),
                    event.getTopic()
            );

        } catch (Exception e) {

            event.setRetryCount(
                    event.getRetryCount() + 1
            );

            event.setLastError(
                    e.getMessage()
            );

            if (event.getRetryCount() >= 10) {
                event.setStatus(OutboxStatus.FAILED);
            }

            repository.save(event);

            log.error(
                    "Failed to publish outbox event: id={}, retryCount={}",
                    event.getId(),
                    event.getRetryCount(),
                    e
            );
        }
    }
}