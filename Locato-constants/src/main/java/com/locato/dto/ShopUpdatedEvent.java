package com.locato.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopUpdatedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventType;
    private LocalDateTime eventTime;

    private String shopId;
    private String businessId;
    private String userId;

    private String shopName;
    private String businessName;
    private String logoUrl;
    private String bannerUrl;

    public String getShopId() {
        return shopId != null ? shopId : businessId;
    }

    public String getShopName() {
        return shopName != null ? shopName : businessName;
    }
}
