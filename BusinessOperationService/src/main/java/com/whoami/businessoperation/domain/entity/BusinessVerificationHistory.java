package com.whoami.businessoperation.domain.entity;

import com.whoami.businessoperation.domain.enums.VerificationAction;
import com.whoami.businessoperation.domain.enums.VerificationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "business_verification_history",
        indexes = {
                @Index(name = "idx_verification_history_business_id", columnList = "business_id"),
                @Index(name = "idx_verification_history_application_id", columnList = "application_id"),
                @Index(name = "idx_verification_history_verification_id", columnList = "verification_id")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessVerificationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "verification_id", nullable = false)
    private UUID verificationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private VerificationAction action;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", length = 40)
    private VerificationStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 40)
    private VerificationStatus newStatus;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}