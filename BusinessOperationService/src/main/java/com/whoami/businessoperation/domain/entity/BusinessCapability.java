package com.whoami.businessoperation.domain.entity;

import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "business_capabilities",
        indexes = {
                @Index(name = "idx_business_capability_business_id", columnList = "business_id"),
                @Index(name = "idx_business_capability_type", columnList = "capability_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_business_capability",
                        columnNames = {"business_id", "capability_type"}
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCapability {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Enumerated(EnumType.STRING)
    @Column(name = "capability_type", nullable = false, length = 40)
    private CapabilityType capabilityType;

    @Enumerated(EnumType.STRING)
    @Column(name = "capability_status", nullable = false, length = 30)
    @Builder.Default
    private CapabilityStatus capabilityStatus = CapabilityStatus.DISABLED;

    @Column(name = "enabled_by")
    private UUID enabledBy;

    @Column(name = "disabled_by")
    private UUID disabledBy;

    @Column(length = 1000)
    private String reason;

    private LocalDateTime enabledAt;

    private LocalDateTime disabledAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();

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