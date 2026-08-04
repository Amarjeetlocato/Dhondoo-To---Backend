package com.whoami.launch.dto;

import java.time.LocalTime;
import java.util.List;

import com.locato.enums.ShopStatus;

public class PublicShopResponse {

    // Shop Info
    private String shopId;
    private String slug;
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

    private ShopStatus shopStatus;

    private Boolean acceptingOrders;
    private Boolean autoMode;

    private LocalTime openingTime;
    private LocalTime closingTime;

    // Statistics
    private Long followers;
    private Double averageRating;
    private Long totalReviews;
    private Long totalProducts;
    private Long totalServices;
    private Long totalReels;

    // Lists
    private List<ProductResponseDTO> products;
    private List<ServiceResponseDTO> services;
    private List<ReelResponseDTO> reels;
    private List<ReviewResponse> reviews;
}