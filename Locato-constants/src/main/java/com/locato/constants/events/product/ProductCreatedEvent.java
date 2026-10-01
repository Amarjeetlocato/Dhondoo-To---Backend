package com.locato.constants.events.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreatedEvent {

    private UUID eventId;
    private ProductEventType eventType;
    private int eventVersion;
    private String source;
    private LocalDateTime occurredAt;
    private String correlationId;

    private String productId;
    private String productName;
    private List<String> productImages;
    private Double productPrice;

    private String businessId;
    private String businessName;
    private String businessLogo;
    private String businessBanner;

    private String userId;
}
