package com.whoami.launch.dto;

import java.time.LocalTime;

import com.whoami.launch.enums.ShopStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ShopResponseDTO {

    private String shopId;
    private String userId;

    private String shopName;
    private String mobileNumber;

    private String address;
    private String village;
    private String block;
    private String district;
    private String state;
    private String country;
    private String pincode;

    private Double latitude;
    private Double longitude;

    private Long totalProducts;
    private Long totalReels;
    private Long totalServices;

    private ShopStatus status;

    private Boolean acceptingOrders;
    private Boolean autoMode;

    private LocalTime openingTime;
    private LocalTime closingTime;
}