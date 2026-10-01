package com.whoami.launch.entity;

import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "customer_profiles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerProfile {

    /**
     * Internal database primary key.
     * Not shared between microservices.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Public/customer identity.
     * Example: CUSTOMER_A7K92M4X
     */
    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String customerId;

    /**
     * User Registry identity.
     * Example: USER_A7K92M4X
     */
    @Column(
            nullable = false,
            unique = true,
            length = 30
    )
    private String userId;

    /**
     * Customer's full name.
     */
    @Column(
            nullable = false,
            length = 100
    )
    private String fullName;

    /**
     * Customer email.
     */
    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;

    /**
     * Customer profile logo.
     */
    @Column
    private String logoUrl;

    /**
     * Customer profile banner.
     */
    @Column
    private String bannerUrl;

    /**
     * Generate customer identity automatically.
     */
    @PrePersist
    protected void prePersist() {

        if (customerId == null) {
            customerId = "CUSTOMER_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }
    }
}