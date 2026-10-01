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
public class ReelDeletedEvent {

    private UUID eventId;
    private String eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String reelId;
    private String reelThumbnail;

    private String businessId;
    private String businessName;
    private String businessLogo;
    private String businessBanner;

    private String userId;
}
