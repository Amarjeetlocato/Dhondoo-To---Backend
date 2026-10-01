package com.whoami.launch.entity;

import java.util.List;
import java.util.UUID;

import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    /**
     * Internal database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public product identity.
     * Example: PRODUCT_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String productId;

    /**
     * Business to which this product belongs.
     * Example: BUSINESS_A7K92M4X
     */
    @Column(
            nullable = false,
            length = 30
    )
    private String businessId;

    @Column(
            nullable = false,
            length = 150
    )
    private String productName;
    

    @ElementCollection
    @CollectionTable(
            name = "product_images",
            joinColumns = @JoinColumn(name = "product_id")
    )
    @Column(name = "image_url")
    private List<String> productImages;

    @Column(columnDefinition = "TEXT")
    private String productDescription;

    private Double productPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus stockStatus = StockStatus.AVAILABLE;

    private String quality;

    private String orderType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductVisibility visibility = ProductVisibility.PUBLIC;

    private String badges;

    @PrePersist
    protected void prePersist() {

        if (productId == null) {
            productId = "PRODUCT_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}