package com.whoami.launch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.*;
import com.whoami.launch.enums.Severity;

@Entity
@Table(name = "admin_audit_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String adminId;

    private String action;

    private String targetId;

    private String ipAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity")
    private Severity severity;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.severity == null) {
            this.severity = Severity.MEDIUM;
        }
    }
}