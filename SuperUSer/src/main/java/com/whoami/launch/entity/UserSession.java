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
 * User Session Tracking Entity
 * Tracks active admin sessions for security monitoring
 */
@Entity
@Table(name = "user_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSession {

    @Id
    @Column(name = "session_id")
    private String id;

    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "token", nullable = false, columnDefinition = "LONGTEXT")
    private String token;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_time", nullable = false)
    private LocalDateTime loginTime;

    @Column(name = "last_access_time")
    private LocalDateTime lastAccessTime;

    @Column(name = "is_active", nullable = false)
    private Boolean active;

    @Column(name = "logout_time")
    private LocalDateTime logoutTime;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
