package com.whoami.launch.order.orders.entity;

import com.whoami.launch.order.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 64
    )
    private String orderId;

    @Column(nullable = false, length = 64)
    private String customerId;

    @Column(nullable = false, length = 64)
    private String businessId;

    @Column(length = 1024)
    private String businessNameSnapshot;

    @Column(length = 2048)
    private String businessLogoSnapshot;

    @Column(length = 2048)
    private String businessBannerSnapshot;

    @Column(name = "business_phone", length = 30)
    private String businessPhone;

    @Column(name = "business_address", length = 2000)
    private String businessAddress;

    @Column(name = "business_lat")
    private Double businessLat;

    @Column(name = "business_lng")
    private Double businessLng;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    private BigDecimal subtotal;

    private BigDecimal deliveryFee;

    @Column(name = "fulfillment_type", length = 50)
    private String fulfillmentType;

    @Column(length = 1000)
    private String customerNote;

    @Column(length = 2000)
    private String deliveryAddress;

    @Column(name = "customer_phone", length = 30)
    private String customerPhone;

    @Column(name = "delivery_partner", length = 100)
    private String deliveryPartner;

    @Column(name = "tracking_id", length = 100)
    private String trackingId;

    @Column(name = "delivery_status", length = 50)
    private String deliveryStatus;

    @Column(name = "delivery_boy_name", length = 150)
    private String deliveryBoyName;

    @Column(name = "delivery_boy_phone", length = 30)
    private String deliveryBoyPhone;

    @Column(name = "delivery_boy_lat")
    private Double deliveryBoyLat;

    @Column(name = "delivery_boy_lng")
    private Double deliveryBoyLng;

    @Column(name = "customer_lat")
    private Double customerLat;

    @Column(name = "customer_lng")
    private Double customerLng;

    @Column(name = "estimated_delivery_time", length = 100)
    private String estimatedDeliveryTime;

    @Column(name = "dispatch_api_url", length = 1000)
    private String dispatchApiUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (orderId == null) {
            orderId = "ORDER_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}