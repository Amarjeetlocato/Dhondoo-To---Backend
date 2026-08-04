package com.whoami.launch.entity;

import java.time.LocalDateTime;

import com.whoami.launch.enums.ReviewStatus;
import com.whoami.launch.enums.ReviewTargetType;
import com.whoami.launch.enums.VerificationType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(
    name = "reviews",
    uniqueConstraints = {
        @UniqueConstraint(
            columnNames = {
                "user_id",
                "target_type",
                "target_id"
            }
        )
    }
)
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReviewTargetType targetType;

    @Column(name = "target_id", nullable = false)
    private String targetId;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType verificationType = VerificationType.NONE;

    @Column(name = "verified_order_id")
    private String verifiedOrderId;

    @Column(name = "verified_booking_id")
    private String verifiedBookingId;
    
    @Enumerated(EnumType.STRING)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
}