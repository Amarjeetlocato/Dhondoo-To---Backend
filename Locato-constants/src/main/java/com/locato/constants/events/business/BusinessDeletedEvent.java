package com.locato.constants.events.business;

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
public class BusinessDeletedEvent {

    private UUID eventId;
    private String eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String businessId;
    private String userId;
    private String businessName;
    private String logoUrl;
    private String bannerUrl;
}
