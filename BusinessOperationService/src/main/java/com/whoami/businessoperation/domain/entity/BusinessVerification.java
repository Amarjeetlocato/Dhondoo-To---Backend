package com.whoami.businessoperation.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.businessoperation.domain.enums.VerificationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "business_verifications",
        indexes = {
                @Index(
                        name = "idx_business_verification_verification_id",
                        columnList = "verification_id"
                ),
                @Index(
                        name = "idx_business_verification_business_id",
                        columnList = "business_id"
                ),
                @Index(
                        name = "idx_business_verification_application_id",
                        columnList = "application_id"
                ),
                @Index(
                        name = "idx_business_verification_status",
                        columnList = "verification_status"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "verification_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String verificationId;

    @Column(
            name = "business_id",
            nullable = false,
            length = 40
    )
    private String businessId;

    @Column(
            name = "application_id",
            nullable = false,
            length = 40
    )
    private String applicationId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "verification_status",
            nullable = false,
            length = 40
    )
    @Builder.Default
    private VerificationStatus verificationStatus =
            VerificationStatus.NOT_STARTED;

    @Column(
            name = "verification_video_url",
            length = 1000
    )
    private String verificationVideoUrl;

    @Column(
            name = "verification_video_public_id",
            length = 500
    )
    private String verificationVideoPublicId;

    @Column(length = 1000)
    private String reviewerComment;

    @Column(name = "reviewed_by", length = 40)
    private String reviewedBy;

    private LocalDateTime startedAt;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime expiresAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (verificationId == null) {
            verificationId =
                    "VERIFICATION_"
                            + UUID.randomUUID()
                                    .toString()
                                    .replace("-", "")
                                    .substring(0, 12)
                                    .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        if (updatedAt == null) {
            updatedAt = now;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}