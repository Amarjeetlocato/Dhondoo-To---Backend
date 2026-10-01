package com.whoami.launch.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "business")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Business {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String businessId;

    @Column(nullable = false, length = 255)
    private String businessName;

    @Column(length = 2048)
    private String imageUrl;

    @Column(length = 2000)
    private String description;

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