package com.whoami.launch.entity;

import java.util.List;

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
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String productId;
    
    @Column(nullable = false)
    private String shopId;
    

    
    @Column(nullable = false)
    private String productName;
    
    @ElementCollection
    @CollectionTable(name = "product_images", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "image_url")
    private List<String> productImages;
    
    @Column(columnDefinition = "TEXT")
    private String productDescription;
    
    @Column
    private Double productPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StockStatus stockStatus = StockStatus.AVAILABLE;
    
    @Column
    private String quality;
    
    @Column
    private String orderType;
    
    private ProductVisibility visibility;
    
    @Column
    private String badges;
    
    // Constructors
   
}
