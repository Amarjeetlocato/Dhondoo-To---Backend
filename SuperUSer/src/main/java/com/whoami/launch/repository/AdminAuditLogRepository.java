package com.whoami.launch.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.AdminAuditLog;
import com.whoami.launch.enums.Severity;

@Repository
public interface AdminAuditLogRepository
        extends JpaRepository<AdminAuditLog, Long> {

    /**
     * Count threats (CRITICAL severity) in last 24 hours
     */
    @Query("""
            SELECT COUNT(a) FROM AdminAuditLog a 
            WHERE a.severity = 'CRITICAL' 
            AND a.createdAt >= :time
            """)
    long countThreats(@Param("time") LocalDateTime time);

    /**
     * Count all threats by severity
     */
    long countBySeverity(Severity severity);

    /**
     * Count threats in time range
     */
    @Query("""
            SELECT COUNT(a) FROM AdminAuditLog a 
            WHERE a.severity = :severity 
            AND a.createdAt >= :startTime 
            AND a.createdAt <= :endTime
            """)
    long countThreatsByTimeRange(
            @Param("severity") Severity severity,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Find all threats by severity
     */
    List<AdminAuditLog> findBySeverityOrderByCreatedAtDesc(Severity severity);

    /**
     * Find recent threats (last n hours)
     */
    @Query("""
            SELECT a FROM AdminAuditLog a 
            WHERE a.severity IN ('HIGH', 'CRITICAL') 
            AND a.createdAt >= :time 
            ORDER BY a.createdAt DESC
            """)
    List<AdminAuditLog> findRecentThreats(@Param("time") LocalDateTime time);

    /**
     * Find audit logs by action
     */
    List<AdminAuditLog> findByActionOrderByCreatedAtDesc(String action);

    /**
     * Find audit logs by admin
     */
    List<AdminAuditLog> findByAdminIdOrderByCreatedAtDesc(String adminId);

    /**
     * Find specific security events (SQL injection, invalid tokens, etc)
     */
    @Query("""
            SELECT a FROM AdminAuditLog a 
            WHERE a.action IN ('INVALID_TOKEN', 'SQL_INJECTION', 'MULTIPLE_TOTP_FAILURE') 
            AND a.createdAt >= :time
            """)
    List<AdminAuditLog> findSecurityEvents(@Param("time") LocalDateTime time);
}
