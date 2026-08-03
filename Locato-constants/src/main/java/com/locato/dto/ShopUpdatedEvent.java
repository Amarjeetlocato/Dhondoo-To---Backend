package com.locato.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShopUpdatedEvent {

    private String eventId;

    private String eventType;

    private LocalDateTime eventTime;

    private String shopId;

    private String userId;

    private String shopName;

    private String mobileNumber;

    private String slug;

    private String logoUrl;

    private String bannerUrl;

    private String changes;
}