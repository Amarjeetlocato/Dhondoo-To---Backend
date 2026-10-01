package com.whoami.launch.dto;

import com.locato.enums.BusinessStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublicBusinessResponse {

    private String businessId;
    private String slug;

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

    private BusinessStatus businessStatus;

    private Boolean acceptingOrders;
    private Boolean autoMode;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Long followers;
    private Double averageRating;
    private Long totalReviews;

    private Long totalProducts;
    private Long totalServices;
    private Long totalReels;

    private List<ProductResponseDTO> products;
    private List<ServiceResponseDTO> services;
    private List<ReelResponseDTO> reels;
    private List<ReviewResponse> reviews;
}