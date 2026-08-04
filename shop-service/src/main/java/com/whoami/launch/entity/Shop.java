package com.whoami.launch.entity;

import java.time.LocalTime;

import com.locato.enums.ShopStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "shops")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Shop {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String shopId;

    @Column(nullable = false, unique = true)
    private String userId;

    @Column(nullable = false)
    private String shopName;
    
    @Column(nullable = false, unique = true)
    private String slug;

    @Column(nullable = false, unique = true)
    private String mobileNumber;

    @Column(nullable = false)
    private String address;

    private String village;

    private String block;

    private String district;

    private String state;

    private String country;

    private String pincode;

    private Double latitude;

    private Double longitude;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShopStatus shopStatus = ShopStatus.OPEN;

    private Boolean acceptingOrders;

    private Boolean autoMode;

    private LocalTime openingTime;

    private LocalTime closingTime;
}
