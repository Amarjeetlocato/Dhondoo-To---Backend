package com.whoami.launch.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.whoami.launch.enums.ReportReason;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "reports")
@Data
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            name = "report_id",
            unique = true,
            nullable = false,
            updatable = false,
            length = 30
    )
    private String reportId;

    /**
     * Business context of the reported content.
     */
    @Column(name = "business_id", length = 30)
    private String businessId;

    /**
     * User who submitted the report.
     */
    @Column(name = "user_id", nullable = false, length = 30)
    private String userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "target_type", nullable = false)
    private ReportTargetType targetType;

    @Column(name = "target_id", nullable = false, length = 50)
    private String targetId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportReason reason;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {

        if (reportId == null) {
            reportId = "REPORT_" +
                    UUID.randomUUID()
                            .toString()
                            .replace("-", "")
                            .substring(0, 12)
                            .toUpperCase();
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}