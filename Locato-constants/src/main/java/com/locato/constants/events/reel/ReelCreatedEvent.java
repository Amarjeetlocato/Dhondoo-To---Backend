package com.locato.constants.events.reel;

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
public class ReelCreatedEvent {

    private UUID eventId;
    private ReelEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String reelId;
    private String reelThumbnail;
    private String reelVideo;
    private String description;

    private String businessId;
    private String businessName;
    private String businessLogo;
    private String businessBanner;

    private String userId;
}
