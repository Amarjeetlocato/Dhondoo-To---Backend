package com.locato.constants.events.customer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerProfileUpdatedEvent {

    private UUID eventId;
    private CustomerEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String userId;
    private String customerId;
    private String fullName;
    private String email;
    private String changes;
}