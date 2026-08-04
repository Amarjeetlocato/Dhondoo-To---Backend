package com.whoami.launch.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.whoami.launch.entity.UserSession;

/**
 * Repository for UserSession entity
 */
@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, String> {

    /**
     * Find all active sessions for an admin
     */
    List<UserSession> findByAdminIdAndActiveTrue(String adminId);

    /**
     * Find all active sessions
     */
    @Query("""
            SELECT s FROM UserSession s
            WHERE s.active = true
            """)
    List<UserSession> findAllActiveSessions();

    /**
     * Count active sessions
     */
    @Query("""
            SELECT COUNT(s) FROM UserSession s
            WHERE s.active = true
            """)
    long countActiveSessions();

    /**
     * Find session by token
     */
    Optional<UserSession> findByToken(String token);

    /**
     * Find active session by admin id and token
     */
    Optional<UserSession> findByAdminIdAndTokenAndActiveTrue(
            String adminId,
            String token);

    /**
     * Count active sessions for specific admin
     */
    long countByAdminIdAndActiveTrue(String adminId);

    /**
     * Find sessions logged in between time range
     */
    @Query("""
            SELECT s FROM UserSession s
            WHERE s.loginTime >= :startTime
            AND s.loginTime <= :endTime
            """)
    List<UserSession> findSessionsByLoginTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Find inactive sessions older than specific time
     */
    @Query("""
            SELECT s FROM UserSession s
            WHERE s.active = false
            AND s.logoutTime <= :expiryTime
            """)
    List<UserSession> findOldInactiveSessions(
            @Param("expiryTime") LocalDateTime expiryTime);

    /**
     * Find sessions from specific IP
     */
    List<UserSession> findByIpAddress(String ipAddress);

    /**
     * Delete old inactive sessions (cleanup)
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
            DELETE FROM UserSession s
            WHERE s.active = false
            AND s.logoutTime <= :expiryTime
            """)
    int deleteOldInactiveSessions(
            @Param("expiryTime") LocalDateTime expiryTime);
}