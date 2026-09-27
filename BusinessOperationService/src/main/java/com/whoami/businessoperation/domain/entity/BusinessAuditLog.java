package com.whoami.businessoperation.domain.entity;

import com.whoami.businessoperation.domain.enums.AuditAction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "business_audit_logs",
        indexes = {
                @Index(name = "idx_business_audit_business_id", columnList = "business_id"),
                @Index(name = "idx_business_audit_application_id", columnList = "application_id"),
                @Index(name = "idx_business_audit_action", columnList = "action"),
                @Index(name = "idx_business_audit_performed_by", columnList = "performed_by")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "business_id", nullable = false)
    private UUID businessId;

    @Column(name = "application_id")
    private UUID applicationId;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false, length = 50)
    private AuditAction action;

    @Column(name = "performed_by")
    private UUID performedBy;

    @Column(name = "performed_by_role", length = 50)
    private String performedByRole;

    @Column(length = 1000)
    private String description;

    @Column(name = "ip_address", length = 100)
    private String ipAddress;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}