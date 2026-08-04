package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopSummaryDTO {

    private String shopId;
    private String shopName;
    private String userId;

    private String village;
    private String district;
    private String pincode;

    private Double latitude;
    private Double longitude;

    private String address;
    private String mobileNumber;
}
