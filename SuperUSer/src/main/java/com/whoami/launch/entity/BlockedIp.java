package com.whoami.launch.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Blocked IP Entity
 * Tracks blocked IP addresses for security purposes
 */
@Entity
@Table(name = "blocked_ips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockedIp {

    @Id
    @Column(name = "id")
    private String id;

    @Column(name = "ip_address", nullable = false, unique = true)
    private String ipAddress;

    @Column(name = "blocked_at", nullable = false)
    private LocalDateTime blockedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "reason", nullable = false)
    private String reason;

    @Column(name = "failed_attempts")
    private Integer failedAttempts;

    @Column(name = "is_permanent")
    private Boolean isPermanent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.isPermanent == null) {
            this.isPermanent = false;
        }
    }

    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if IP block is still active
     */
    public boolean isActive() {
        if (isPermanent) {
            return true;
        }
        return expiresAt != null && LocalDateTime.now().isBefore(expiresAt);
    }
}
