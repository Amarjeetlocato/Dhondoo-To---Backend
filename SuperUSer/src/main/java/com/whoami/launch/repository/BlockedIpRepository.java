package com.whoami.launch.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.BlockedIp;

import jakarta.transaction.Transactional;

/**
 * Repository for BlockedIp entity
 */
@Repository
public interface BlockedIpRepository extends JpaRepository<BlockedIp, String> {

    /**
     * Check if IP is blocked and active
     */
    @Query("""
            SELECT COUNT(b) > 0 FROM BlockedIp b 
            WHERE b.ipAddress = :ipAddress 
            AND (b.isPermanent = true OR b.expiresAt > CURRENT_TIMESTAMP)
            """)
    boolean isIpBlocked(@Param("ipAddress") String ipAddress);

    /**
     * Find blocked IP by address if active
     */
    @Query("""
            SELECT b FROM BlockedIp b 
            WHERE b.ipAddress = :ipAddress 
            AND (b.isPermanent = true OR b.expiresAt > CURRENT_TIMESTAMP)
            """)
    Optional<BlockedIp> findActiveBlockedIp(@Param("ipAddress") String ipAddress);

    /**
     * Check if IP exists (blocked or not)
     */
    boolean existsByIpAddress(String ipAddress);

    /**
     * Find IP by address
     */
    Optional<BlockedIp> findByIpAddress(String ipAddress);

    /**
     * Get all currently active blocked IPs
     */
    @Query("""
            SELECT b FROM BlockedIp b 
            WHERE b.isPermanent = true OR b.expiresAt > CURRENT_TIMESTAMP
            ORDER BY b.blockedAt DESC
            """)
    List<BlockedIp> findAllActiveBlockedIps();

    /**
     * Get all blocked IPs
     */
    @Query("""
            SELECT b FROM BlockedIp b ORDER BY b.blockedAt DESC
            """)
    List<BlockedIp> findAllBlockedIps();

    /**
     * Count active blocked IPs
     */
    @Query("""
            SELECT COUNT(b) FROM BlockedIp b 
            WHERE b.isPermanent = true OR b.expiresAt > CURRENT_TIMESTAMP
            """)
    long countActiveBlockedIps();

    /**
     * Find blocked IPs by reason
     */
    List<BlockedIp> findByReason(String reason);

    /**
     * Find blocked IPs within time range
     */
    @Query("""
            SELECT b FROM BlockedIp b 
            WHERE b.blockedAt >= :startTime 
            AND b.blockedAt <= :endTime
            ORDER BY b.blockedAt DESC
            """)
    List<BlockedIp> findBlockedIpsByTimeRange(
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime);

    /**
     * Delete expired IP blocks
     */
    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Transactional
    @Query("""
            DELETE FROM BlockedIp b
            WHERE b.isPermanent = false
            AND b.expiresAt <= CURRENT_TIMESTAMP
            """)
    int deleteExpiredBlocks();

    /**
     * Find IPs blocked for specific reason
     */
    @Query("""
            SELECT b FROM BlockedIp b WHERE b.reason = :reason
            """)
    List<BlockedIp> findByBlockedReason(@Param("reason") String reason);
}
