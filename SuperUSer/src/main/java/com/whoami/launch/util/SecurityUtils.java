package com.whoami.launch.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.whoami.launch.service.SecurityService;
import com.whoami.launch.enums.Severity;
import com.whoami.launch.entity.AdminAuditLog;
import com.whoami.launch.repository.AdminAuditLogRepository;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Security Utilities
 * Helper methods for security operations
 */
@Component
public class SecurityUtils {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private AdminAuditLogRepository auditRepository;

    /**
     * Get client IP address from request
     */
    public static String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                    RequestContextHolder.getRequestAttributes();
            
            if (attributes == null) {
                return "UNKNOWN";
            }

            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }

            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }

            return ip.split(",")[0].trim();

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Get user agent from request
     */
    public static String getUserAgent() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) 
                    RequestContextHolder.getRequestAttributes();
            
            if (attributes == null) {
                return "UNKNOWN";
            }

            HttpServletRequest request = attributes.getRequest();
            return request.getHeader("User-Agent");

        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    /**
     * Log security event to audit
     */
    public void logSecurityEvent(String adminId, String action, String targetId, 
                                 String ipAddress, Severity severity, String description) {
        try {
            AdminAuditLog audit = AdminAuditLog.builder()
                    .adminId(adminId)
                    .action(action)
                    .targetId(targetId)
                    .ipAddress(ipAddress)
                    .severity(severity)
                    .description(description)
                    .build();

            auditRepository.save(audit);
            System.out.println("[AUDIT] Logged event: " + action + " - Severity: " + severity);

        } catch (Exception e) {
            System.out.println("[AUDIT] Error logging security event: " + e.getMessage());
        }
    }

    /**
     * Track failed login attempt
     */
    public void trackFailedLogin(String ipAddress, String adminId) {
        securityService.trackLoginAttempt(ipAddress);
        securityService.checkAndBlockIfNeeded(ipAddress);

        // Log security event
        logSecurityEvent(
                adminId,
                "FAILED_LOGIN",
                adminId,
                ipAddress,
                Severity.MEDIUM,
                "Failed login attempt - Attempts: " + securityService.getFailedAttempts(ipAddress)
        );
    }

    /**
     * Track successful login
     */
    public void trackSuccessfulLogin(String adminId, String token, String ipAddress, String userAgent) {
        // Reset failed attempts
        securityService.resetLoginAttempts(ipAddress);

        // Save session
        securityService.saveSession(adminId, "", token, ipAddress, userAgent);

        // Log security event
        logSecurityEvent(
                adminId,
                "LOGIN_SUCCESS",
                adminId,
                ipAddress,
                Severity.LOW,
                "Successful login from IP: " + ipAddress
        );
    }

    /**
     * Track logout
     */
    public void trackLogout(String adminId, String ipAddress) {
        logSecurityEvent(
                adminId,
                "LOGOUT",
                adminId,
                ipAddress,
                Severity.LOW,
                "Admin logged out"
        );
    }

    /**
     * Log invalid token attempt
     */
    public void logInvalidTokenAttempt(String ipAddress, String tokenPart) {
        logSecurityEvent(
                null,
                "INVALID_TOKEN",
                null,
                ipAddress,
                Severity.CRITICAL,
                "Invalid JWT token attempt: " + tokenPart
        );
    }

    /**
     * Log SQL injection attempt
     */
    public void logSqlInjectionAttempt(String ipAddress, String parameter) {
        logSecurityEvent(
                null,
                "SQL_INJECTION",
                null,
                ipAddress,
                Severity.CRITICAL,
                "Potential SQL Injection in parameter: " + parameter
        );
    }

    /**
     * Log TOTP failure
     */
    public void logTotpFailure(String adminId, String ipAddress, int attempts) {
        logSecurityEvent(
                adminId,
                "TOTP_FAILURE",
                adminId,
                ipAddress,
                attempts >= 3 ? Severity.HIGH : Severity.MEDIUM,
                "TOTP verification failed - Attempts: " + attempts
        );
    }

    /**
     * Log suspicious activity
     */
    public void logSuspiciousActivity(String adminId, String activity, String ipAddress, Severity severity) {
        logSecurityEvent(
                adminId,
                "SUSPICIOUS_ACTIVITY",
                adminId,
                ipAddress,
                severity,
                activity
        );
    }
}
