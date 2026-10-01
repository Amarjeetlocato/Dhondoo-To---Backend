package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Column(
            name = "review_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String reviewId;

    /**
     * Business to which the reviewed content belongs.
     */
    @Column(name = "business_id", nullable = false, length = 30)
    private String businessId;

    /**
     * User who created the review.
     */
    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReviewTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 50)
    private String targetId;

    @Column(nullable = false)
    private Integer rating;

    @Column(name = "review_text", columnDefinition = "TEXT")
    private String reviewText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VerificationType verificationType = VerificationType.NONE;

    @Column(name = "verified_order_id", length = 30)
    private String verifiedOrderId;

    @Column(name = "verified_booking_id", length = 30)
    private String verifiedBookingId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReviewStatus status = ReviewStatus.ACTIVE;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        if (reviewId == null) {
            reviewId = "REVIEW_" +
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