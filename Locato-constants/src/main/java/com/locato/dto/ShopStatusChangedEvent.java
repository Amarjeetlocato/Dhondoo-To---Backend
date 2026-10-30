package com.locato.dto;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.locato.enums.ShopStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShopStatusChangedEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    private String eventId;
    private String eventType;
    private LocalDateTime eventTime;

    private String shopId;
    private String businessId;
    private String userId;

    private String logoUrl;
    private String bannerUrl;
    private String shopName;
    private String businessName;

    private ShopStatus previousStatus;
    private ShopStatus shopStatus;

    public String getShopId() {
        return shopId != null ? shopId : businessId;
    }

    public String getShopName() {
        return shopName != null ? shopName : businessName;
    }
}
