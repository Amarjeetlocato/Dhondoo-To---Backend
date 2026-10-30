package com.whoami.businessoperation.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "business_update_requests",
        indexes = {
                @Index(name = "idx_update_req_business_id", columnList = "business_id"),
                @Index(name = "idx_update_req_status", columnList = "status")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessUpdateRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", unique = true, nullable = false, length = 100)
    private String requestId;

    @Column(name = "business_id", nullable = false, length = 100)
    private String businessId;

    @Column(name = "requested_business_name", length = 150)
    private String requestedBusinessName;

    @Column(name = "requested_phone", length = 30)
    private String requestedPhone;

    @Column(name = "requested_email", length = 150)
    private String requestedEmail;

    @Column(name = "requested_address", length = 500)
    private String requestedAddress;

    @Column(name = "requested_latitude", precision = 10, scale = 7)
    private BigDecimal requestedLatitude;

    @Column(name = "requested_longitude", precision = 10, scale = 7)
    private BigDecimal requestedLongitude;

    @Column(length = 500)
    private String reason;

    @Column(length = 50)
    @Builder.Default
    private String status = "PENDING_APPROVAL";

    @Column(name = "reviewed_by", length = 100)
    private String reviewedBy;

    private LocalDateTime reviewedAt;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.requestId == null) {
            this.requestId = "REQ_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }
}
