package com.whoami.businessoperation.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.businessoperation.domain.enums.VerificationAction;
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
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "business_verification_history",
        indexes = {
                @Index(
                        name = "idx_verification_history_history_id",
                        columnList = "history_id"
                ),
                @Index(
                        name = "idx_verification_history_business_id",
                        columnList = "business_id"
                ),
                @Index(
                        name = "idx_verification_history_application_id",
                        columnList = "application_id"
                ),
                @Index(
                        name = "idx_verification_history_verification_id",
                        columnList = "verification_id"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "history_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String historyId;

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

    @Column(
            name = "verification_id",
            nullable = false,
            length = 40
    )
    private String verificationId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "action",
            nullable = false,
            length = 50
    )
    private VerificationAction action;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "previous_status",
            length = 40
    )
    private VerificationStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "new_status",
            length = 40
    )
    private VerificationStatus newStatus;

    @Column(
            name = "performed_by",
            length = 40
    )
    private String performedBy;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (historyId == null) {
            historyId =
                    "HISTORY_"
                            + UUID.randomUUID()
                                    .toString()
                                    .replace("-", "")
                                    .substring(0, 12)
                                    .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = now;
        }
    }
}