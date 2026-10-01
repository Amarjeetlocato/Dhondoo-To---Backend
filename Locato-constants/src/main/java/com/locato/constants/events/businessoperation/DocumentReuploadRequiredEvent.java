package com.locato.constants.events.businessoperation;

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
public class DocumentReuploadRequiredEvent {

    private UUID eventId;
    private BusinessOperationEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String businessId;
    private String applicationId;
    private String documentId;

    private String performedBy;
    private String performedByRole;

    private String reason;
}