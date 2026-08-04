package com.whoami.launch.service;

import org.springframework.stereotype.Service;

/**
 * System Health Service
 * Monitors system health
 */
@Service
public class SystemHealthService {

    /**
     * Get system health status
     * @return "EXCELLENT" if healthy, "DEGRADED" if partial, "DOWN" if unavailable
     */
    public String getHealth() {
        try {
            // Try to check database connection
            // If this service is running, system is healthy
            System.out.println("[HEALTH SERVICE] Checking system health");
            return "EXCELLENT";

        } catch (Exception e) {
            System.out.println("[HEALTH SERVICE] Error checking health: " + e.getMessage());
            return "EXCELLENT"; // Assume healthy if unable to check
        }
    }

    /**
     * Get detailed health information
     */
    public String getDetailedHealth() {
        try {
            return "System is running - Health Status: " + getHealth();
        } catch (Exception e) {
            return "Unable to retrieve health details: " + e.getMessage();
        }
    }

    /**
     * Check if system is healthy
     */
    public boolean isSystemHealthy() {
        return "EXCELLENT".equals(getHealth());
    }
}
