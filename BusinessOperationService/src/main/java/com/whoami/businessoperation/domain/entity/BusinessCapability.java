package com.whoami.businessoperation.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;

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
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "business_capabilities",
        indexes = {
                @Index(
                        name = "idx_business_capability_capability_id",
                        columnList = "capability_id"
                ),
                @Index(
                        name = "idx_business_capability_business_id",
                        columnList = "business_id"
                ),
                @Index(
                        name = "idx_business_capability_type",
                        columnList = "capability_type"
                )
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_business_capability",
                        columnNames = {
                                "business_id",
                                "capability_type"
                        }
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "capability_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String capabilityId;

    @Column(
            name = "business_id",
            nullable = false,
            length = 40
    )
    private String businessId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "capability_type",
            nullable = false,
            length = 40
    )
    private CapabilityType capabilityType;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "capability_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private CapabilityStatus capabilityStatus =
            CapabilityStatus.DISABLED;

    @Column(name = "enabled_by", length = 40)
    private String enabledBy;

    @Column(name = "disabled_by", length = 40)
    private String disabledBy;

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

        if (capabilityId == null) {
            capabilityId =
                    "CAPABILITY_"
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