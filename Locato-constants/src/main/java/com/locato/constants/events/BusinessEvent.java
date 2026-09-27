package com.locato.constants.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessEvent {

    private UUID eventId;

    private BusinessEventType eventType;

    private UUID businessId;

    private UUID applicationId;

    private UUID ownerUserId;

    private UUID performedBy;

    private String performedByRole;

    private String description;

    private String reason;

    private LocalDateTime occurredAt;
}