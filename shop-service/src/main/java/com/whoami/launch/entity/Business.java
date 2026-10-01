package com.whoami.launch.entity;

import java.time.LocalTime;
import java.util.UUID;

import com.locato.enums.BusinessStatus;
import com.locato.enums.BusinessType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "businesses")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Business {

    /**
     * Internal database primary key.
     * Not shared between microservices.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public business identity.
     * Example: BUSINESS_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String businessId;

    /**
     * User Registry identity of the business owner.
     *
     * One user can own multiple businesses.
     */
    @Column(
            nullable = false,
            length = 30
    )
    private String userId;

    /**
     * Business name.
     */
    @Column(
            nullable = false,
            length = 150
    )
    private String businessName;

    /**
     * Type/category of the business.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessType businessType;

    /**
     * Public unique URL/search slug.
     */
    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String slug;

    /**
     * Business contact number.
     */
    @Column(
            nullable = false,
            unique = true,
            length = 20
    )
    private String mobileNumber;

    /**
     * Business address.
     */
    @Column(
            nullable = false,
            length = 500
    )
    private String address;

    private String village;

    private String block;

    private String district;

    private String state;

    private String country;

    private String pincode;

    /**
     * Location coordinates used for
     * location-based business discovery.
     */
    @Column(nullable = false)
    private Double latitude;

    @Column(nullable = false)
    private Double longitude;

    /**
     * Current business operating status.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BusinessStatus businessStatus = BusinessStatus.OPEN;

    /**
     * Whether the business accepts orders.
     */
    @Column(nullable = false)
    private Boolean acceptingOrders = true;

    /**
     * Whether orders are processed automatically.
     */
    @Column(nullable = false)
    private Boolean autoMode = false;

    /**
     * Business operating hours.
     */
    private LocalTime openingTime;

    private LocalTime closingTime;

    /**
     * Generate business identity automatically.
     */
    @PrePersist
    protected void prePersist() {

        if (businessId == null) {
            businessId = "BUSINESS_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}