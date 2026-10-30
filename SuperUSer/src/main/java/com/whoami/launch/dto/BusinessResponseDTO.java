package com.whoami.launch.dto;

import java.time.LocalDateTime;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BusinessResponseDTO {

    private String businessId;
    private String shopId;
    private String userId;

    private String businessName;
    private String shopName;
    private String mobileNumber;

    private String logoUrl;
    private String bannerUrl;
    private String category;

    private String address;
    private String village;
    private String block;
    private String district;
    private String state;
    private String country;
    private String pincode;

    private Double latitude;
    private Double longitude;

    private String verificationStatus; // PENDING, APPROVED, REJECTED
    private String rejectionReason;
    private String gstNumber;
    private String panNumber;
    private String documentsUrl;

    private Long totalProducts;
    private Long totalReels;
    private Long totalServices;

    private String status;
    private Boolean acceptingOrders;
    private Boolean autoMode;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Double rating;
    private Long followerCount;
    private LocalDateTime createdAt;

    // Helper compatibility getters/setters
    public String getShopId() {
        return shopId != null ? shopId : businessId;
    }

    public String getShopName() {
        return shopName != null ? shopName : businessName;
    }
}
