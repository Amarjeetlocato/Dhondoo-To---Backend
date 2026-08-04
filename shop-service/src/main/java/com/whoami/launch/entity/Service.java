package com.whoami.launch.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "services")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Service {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String serviceId;
    
    @Column(nullable = false)
    private String shopId;
    
    
    @Column(nullable = false)
    private String serviceName;
    
    @Column
    private String thumbnailUrl;

    @Column
    private String thumbnailPublicId;

    @Column
    private String promoVideoUrl;

    @Column
    private String videoPublicId;
    
    @Column(columnDefinition = "TEXT")
    private String serviceDescription;
    
    @Column
    private Double price;
    
    @Column
    private String duration;
    
    @Column
    private String orderType;
    
    @Column
    private String suggestion;
    
    @Column
    private String visibility;
    
    @Column
    private String badges;
   
}
