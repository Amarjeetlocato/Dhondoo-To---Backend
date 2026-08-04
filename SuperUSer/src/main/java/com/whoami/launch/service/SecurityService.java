package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.SecurityStatsResponse;
import com.whoami.launch.entity.BlockedIp;
import com.whoami.launch.entity.UserSession;
import com.whoami.launch.enums.Severity;
import com.whoami.launch.repository.AdminAuditLogRepository;
import com.whoami.launch.repository.BlockedIpRepository;
import com.whoami.launch.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;

/**
 * Security Service
 * Handles security operations like session tracking, IP blocking, threat detection
 */
@Service
@RequiredArgsConstructor
public class SecurityService {

    @Autowired
    private final UserSessionRepository sessionRepository;

    @Autowired
    private final BlockedIpRepository blockedIpRepository;

    @Autowired
    private final AdminAuditLogRepository auditRepository;

    @Autowired
    private final SystemHealthService healthService;

    // In-memory tracking of failed login attempts
    private final Map<String, Integer> loginAttempts = new ConcurrentHashMap<>();

    /**
     * Track login attempt
     */
    public void trackLoginAttempt(String ipAddress) {
        loginAttempts.merge(ipAddress, 1, Integer::sum);
    }

    /**
     * Reset login attempts after successful login
     */
    public void resetLoginAttempts(String ipAddress) {
        loginAttempts.remove(ipAddress);
    }

    /**
     * Check if IP should be blocked due to failed attempts
     */
    public void checkAndBlockIfNeeded(String ipAddress) {
        Integer attempts = loginAttempts.getOrDefault(ipAddress, 0);

        // Block after 20 failed attempts
        if (attempts >= 20) {
            blockIpAddress(
                    ipAddress,
                    "Too many failed login attempts (" + attempts + ")",
                    24 // 24 hours
            );
            loginAttempts.remove(ipAddress);
        }
    }

    /**
     * Get failed login attempts for IP
     */
    public int getFailedAttempts(String ipAddress) {
        return loginAttempts.getOrDefault(ipAddress, 0);
    }

    /**
     * Block an IP address
     */
    public void blockIpAddress(String ipAddress, String reason, int expiryHours) {
        try {
            // Check if already blocked
            if (blockedIpRepository.existsByIpAddress(ipAddress)) {
                BlockedIp existing = blockedIpRepository.findByIpAddress(ipAddress)
                        .orElse(null);
                if (existing != null) {
                    existing.setFailedAttempts((existing.getFailedAttempts() != null 
                            ? existing.getFailedAttempts() : 0) + 1);
                    blockedIpRepository.save(existing);
                }
                return;
            }

            BlockedIp blocked = BlockedIp.builder()
                    .id(UUID.randomUUID().toString())
                    .ipAddress(ipAddress)
                    .blockedAt(LocalDateTime.now())
                    .expiresAt(LocalDateTime.now().plusHours(expiryHours))
                    .reason(reason)
                    .failedAttempts(loginAttempts.getOrDefault(ipAddress, 1))
                    .isPermanent(false)
                    .build();

            blockedIpRepository.save(blocked);

            System.out.println("[SECURITY] IP blocked: " + ipAddress + " - Reason: " + reason);

        } catch (Exception e) {
            System.out.println("[SECURITY] Error blocking IP: " + e.getMessage());
        }
    }

    /**
     * Unblock an IP address
     */
    public void unblockIpAddress(String ipAddress) {
        try {
            blockedIpRepository.findByIpAddress(ipAddress)
                    .ifPresent(blockedIpRepository::delete);
            System.out.println("[SECURITY] IP unblocked: " + ipAddress);
        } catch (Exception e) {
            System.out.println("[SECURITY] Error unblocking IP: " + e.getMessage());
        }
    }

    /**
     * Check if IP is blocked
     */
    public boolean isIpBlocked(String ipAddress) {
        return blockedIpRepository.isIpBlocked(ipAddress);
    }

    /**
     * Save user session
     */
    public UserSession saveSession(String adminId, String email, String token, 
                                   String ipAddress, String userAgent) {
        try {
            UserSession session = UserSession.builder()
                    .id(UUID.randomUUID().toString())
                    .adminId(adminId)
                    .email(email)
                    .token(token)
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                    .loginTime(LocalDateTime.now())
                    .lastAccessTime(LocalDateTime.now())
                    .active(true)
                    .build();

            UserSession saved = sessionRepository.save(session);
            System.out.println("[SECURITY] Session saved for: " + adminId);
            return saved;

        } catch (Exception e) {
            System.out.println("[SECURITY] Error saving session: " + e.getMessage());
            return null;
        }
    }

    /**
     * Logout session
     */
    public void logoutSession(String sessionId) {
        try {
            sessionRepository.findById(sessionId).ifPresent(session -> {
                session.setActive(false);
                session.setLogoutTime(LocalDateTime.now());
                sessionRepository.save(session);
                System.out.println("[SECURITY] Session logged out: " + sessionId);
            });
        } catch (Exception e) {
            System.out.println("[SECURITY] Error logging out session: " + e.getMessage());
        }
    }

    /**
     * Get security statistics
     */
    public SecurityStatsResponse getSecurityStats() {
        try {
            long activeSessions = sessionRepository.countActiveSessions();
            long blockedIps = blockedIpRepository.countActiveBlockedIps();
            long threatsDetected = auditRepository.countThreats(
                    LocalDateTime.now().minusHours(24)
            );

            long critical = auditRepository.countBySeverity(Severity.CRITICAL);
            long high = auditRepository.countBySeverity(Severity.HIGH);
            long medium = auditRepository.countBySeverity(Severity.MEDIUM);
            long low = auditRepository.countBySeverity(Severity.LOW);

            return SecurityStatsResponse.builder()
                    .activeSessions(activeSessions)
                    .blockedIps(blockedIps)
                    .threatsDetected(threatsDetected)
                    .totalSessions(sessionRepository.count())
                    .systemHealth(healthService.getHealth())
                    .criticalThreats(critical)
                    .highThreats(high)
                    .mediumThreats(medium)
                    .lowThreats(low)
                    .build();

        } catch (Exception e) {
            System.out.println("[SECURITY] Error fetching stats: " + e.getMessage());
            return SecurityStatsResponse.builder()
                    .systemHealth("UNKNOWN")
                    .build();
        }
    }

    /**
     * Clean up old sessions and expired blocks
     * Runs automatically every hour (3600000ms)
     */
    @Scheduled(fixedDelay = 3600000)
    public void cleanup() {
        try {
            // Delete old inactive sessions (older than 30 days)
            sessionRepository.deleteOldInactiveSessions(
                    LocalDateTime.now().minusDays(30)
            );

            // Delete expired IP blocks
            blockedIpRepository.deleteExpiredBlocks();

            System.out.println("[SECURITY] Scheduled cleanup completed at " + LocalDateTime.now());

        } catch (Exception e) {
            System.out.println("[SECURITY] Error during cleanup: " + e.getMessage());
        }
    }

    /**
     * Get all active sessions
     */
    public long getActiveSessions() {
        return sessionRepository.countActiveSessions();
    }

    /**
     * Get blocked IPs count
     */
    public long getBlockedIpsCount() {
        return blockedIpRepository.countActiveBlockedIps();
    }

    /**
     * Get recent threats
     */
    public long getRecentThreats(int hours) {
        return auditRepository.countThreats(
                LocalDateTime.now().minusHours(hours)
        );
    }
}
