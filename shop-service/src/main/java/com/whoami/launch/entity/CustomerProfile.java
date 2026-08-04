package com.whoami.launch.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String customerId;
    
    @Column(nullable = false, unique = true)
    private String userId;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column
    private String logoUrl;
    
    @Column
    private String bannerUrl;
    
    
}
