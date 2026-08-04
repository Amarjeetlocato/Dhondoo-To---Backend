package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyShopDTO {
    private String shopId;
    private String shopName;
    private String userId;
    private String address;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private String mobileNumber;
}
