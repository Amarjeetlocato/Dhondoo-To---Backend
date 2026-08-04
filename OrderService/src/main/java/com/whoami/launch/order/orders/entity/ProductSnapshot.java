package com.whoami.launch.order.orders.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product_snapshots")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSnapshot {

    @Id
    private String productId;

    private String shopId;

    private String shopName;

    private String userId;

    private String productName;

    private Double productPrice;
}