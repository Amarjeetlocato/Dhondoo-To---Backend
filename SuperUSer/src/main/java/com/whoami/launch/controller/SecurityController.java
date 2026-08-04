package com.whoami.launch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.SecurityStatsResponse;
import com.whoami.launch.service.SecurityService;

/**
 * Security Controller
 * Endpoints for security monitoring and management
 */
@RestController
@RequestMapping("/api/admin/security")
public class SecurityController {

    @Autowired
    private SecurityService securityService;

    /**
     * Get security statistics
     * GET /api/admin/security/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<SecurityStatsResponse>> getSecurityStats() {
        try {
            SecurityStatsResponse stats = securityService.getSecurityStats();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Security statistics fetched successfully",
                            stats
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching security stats: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get active sessions count
     * GET /api/admin/security/active-sessions
     */
    @GetMapping("/active-sessions")
    public ResponseEntity<ApiResponse<Long>> getActiveSessions() {
        try {
            long activeSessions = securityService.getActiveSessions();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Active sessions count: " + activeSessions,
                            activeSessions
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching active sessions: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get blocked IPs count
     * GET /api/admin/security/blocked-ips
     */
    @GetMapping("/blocked-ips")
    public ResponseEntity<ApiResponse<Long>> getBlockedIps() {
        try {
            long blockedIps = securityService.getBlockedIpsCount();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Blocked IPs count: " + blockedIps,
                            blockedIps
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching blocked IPs: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get recent threats
     * GET /api/admin/security/threats?hours=24
     */
    @GetMapping("/threats")
    public ResponseEntity<ApiResponse<Long>> getThreats(
            @RequestParam(value = "hours", defaultValue = "24") int hours) {
        try {
            long threats = securityService.getRecentThreats(hours);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Threats detected in last " + hours + " hours: " + threats,
                            threats
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching threats: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Check if IP is blocked
     * GET /api/admin/security/check-ip?ip=192.168.1.1
     */
    @GetMapping("/check-ip")
    public ResponseEntity<ApiResponse<Boolean>> checkIpBlocked(
            @RequestParam(value = "ip") String ipAddress) {
        try {
            boolean isBlocked = securityService.isIpBlocked(ipAddress);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "IP block status: " + (isBlocked ? "BLOCKED" : "ALLOWED"),
                            isBlocked
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error checking IP: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Get failed login attempts for IP
     * GET /api/admin/security/failed-attempts?ip=192.168.1.1
     */
    @GetMapping("/failed-attempts")
    public ResponseEntity<ApiResponse<Integer>> getFailedAttempts(
            @RequestParam(value = "ip") String ipAddress) {
        try {
            int attempts = securityService.getFailedAttempts(ipAddress);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Failed login attempts for IP: " + attempts,
                            attempts
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error fetching failed attempts: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Block an IP address
     * POST /api/admin/security/block-ip?ip=192.168.1.1&reason=Brute%20Force&hours=24
     */
    @PostMapping("/block-ip")
    public ResponseEntity<ApiResponse<String>> blockIp(
            @RequestParam(value = "ip") String ipAddress,
            @RequestParam(value = "reason", defaultValue = "Manual block") String reason,
            @RequestParam(value = "hours", defaultValue = "24") int expiryHours) {
        try {
            securityService.blockIpAddress(ipAddress, reason, expiryHours);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "IP blocked successfully",
                            ipAddress
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error blocking IP: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Unblock an IP address
     * POST /api/admin/security/unblock-ip?ip=192.168.1.1
     */
    @PostMapping("/unblock-ip")
    public ResponseEntity<ApiResponse<String>> unblockIp(
            @RequestParam(value = "ip") String ipAddress) {
        try {
            securityService.unblockIpAddress(ipAddress);

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "IP unblocked successfully",
                            ipAddress
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error unblocking IP: " + e.getMessage(),
                            null
                    ));
        }
    }

    /**
     * Manual security cleanup
     * POST /api/admin/security/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<ApiResponse<String>> cleanup() {
        try {
            securityService.cleanup();

            return ResponseEntity.ok(
                    new ApiResponse<>(
                            true,
                            "Security cleanup completed successfully",
                            null
                    )
            );

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(
                            false,
                            "Error during cleanup: " + e.getMessage(),
                            null
                    ));
        }
    }
}
