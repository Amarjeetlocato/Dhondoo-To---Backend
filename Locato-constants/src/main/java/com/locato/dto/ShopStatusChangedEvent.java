package com.locato.dto;

import java.time.LocalDateTime;

import com.locato.enums.ShopStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopStatusChangedEvent {

    private String eventId;

    private String eventType;

    private LocalDateTime eventTime;

    private String shopId;

    private String userId;

    private String logoUrl;
    private String bannerUrl;
    private String shopName;

    private ShopStatus previousStatus;

    private ShopStatus shopStatus;
}