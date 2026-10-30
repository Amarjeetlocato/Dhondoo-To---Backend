package com.locato.constants.events.businessoperation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationCreationRequestedEvent {

    private UUID eventId;
    private BusinessOperationEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String businessId;
    private String userId;

    private String businessType;
    private String businessName;
    private String description;
    private String phone;
    private String email;
    private String address;

    private BigDecimal latitude;
    private BigDecimal longitude;
}
