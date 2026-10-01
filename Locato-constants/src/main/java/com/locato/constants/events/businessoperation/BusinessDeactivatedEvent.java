package com.locato.constants.events.businessoperation;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDeactivatedEvent {

    private UUID eventId;
    private BusinessOperationEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String businessId;

    private String performedBy;
    private String performedByRole;

    private String reason;
}
