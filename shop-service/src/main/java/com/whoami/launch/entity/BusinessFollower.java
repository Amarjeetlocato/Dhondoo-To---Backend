package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
        name = "business_followers",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "business_id",
                                "user_id"
                        }
                )
        }
)
@Data
public class BusinessFollower {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "follow_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 35
    )
    private String followId;

    @Column(name = "business_id", nullable = false, length = 30)
    private String businessId;

    /**
     * User who follows the business.
     */
    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Column(nullable = false)
    private LocalDateTime followedAt;

    @PrePersist
    protected void prePersist() {

        if (followId == null) {
            followId = "BUSINESS_FOLLOW_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (followedAt == null) {
            followedAt = LocalDateTime.now();
        }
    }
}