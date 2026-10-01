package com.whoami.launch.dto;

import com.locato.enums.BusinessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BusinessResponseDTO {

    private String businessId;
    private String userId;

    private String businessName;
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

    private BusinessStatus status;

    private Boolean acceptingOrders;
    private Boolean autoMode;

    private LocalTime openingTime;
    private LocalTime closingTime;
}

