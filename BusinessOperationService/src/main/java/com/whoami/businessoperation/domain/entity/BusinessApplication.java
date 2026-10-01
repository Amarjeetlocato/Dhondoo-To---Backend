package com.whoami.businessoperation.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.businessoperation.domain.enums.BusinessApplicationStatus;
import com.whoami.businessoperation.domain.enums.BusinessOperationalStatus;
import com.whoami.businessoperation.domain.enums.BusinessType;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "business_applications",
        indexes = {
                @Index(
                        name = "idx_business_application_business_id",
                        columnList = "business_id"
                ),
                @Index(
                        name = "idx_business_application_owner_id",
                        columnList = "owner_user_id"
                ),
                @Index(
                        name = "idx_business_application_status",
                        columnList = "application_status"
                )
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessApplication {

    /*
     * Internal database primary key.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     * Public application identity.
     * Example: APPLICATION_A7K92M4X
     */
    @Column(
            name = "application_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 40
    )
    private String applicationId;

    /*
     * Public Business identity.
     * Example: BUSINESS_A7K92M4X
     */
    @Column(
            name = "business_id",
            nullable = false,
            unique = true,
            length = 40
    )
    private String businessId;

    /*
     * User Registry public identity.
     * Example: USER_A7K92M4X
     */
    @Column(
            name = "owner_user_id",
            nullable = false,
            length = 40
    )
    private String ownerUserId;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "business_type",
            nullable = false,
            length = 50
    )
    private BusinessType businessType;

    @Column(
            name = "business_name",
            nullable = false,
            length = 150
    )
    private String businessName;

    @Column(length = 1000)
    private String description;

    @Column(length = 30)
    private String phone;

    @Column(length = 150)
    private String email;

    @Column(length = 500)
    private String address;

    @Column(precision = 10, scale = 7)
    private BigDecimal latitude;

    @Column(precision = 10, scale = 7)
    private BigDecimal longitude;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "application_status",
            nullable = false,
            length = 50
    )
    @Builder.Default
    private BusinessApplicationStatus applicationStatus =
            BusinessApplicationStatus.DRAFT;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "operational_status",
            nullable = false,
            length = 30
    )
    @Builder.Default
    private BusinessOperationalStatus operationalStatus =
            BusinessOperationalStatus.CREATED;

    private LocalDateTime submittedAt;

    private LocalDateTime approvedAt;

    private LocalDateTime rejectedAt;

    private LocalDateTime suspendedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        LocalDateTime now = LocalDateTime.now();

        if (applicationId == null) {
            applicationId = "APPLICATION_" +
                    UUID.randomUUID()
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