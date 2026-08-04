package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Security Statistics Response DTO
 * Contains security metrics for dashboard
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecurityStatsResponse {

    private long activeSessions;

    private long blockedIps;

    private long threatsDetected;

    private String systemHealth;

    private long totalSessions;

    private long criticalThreats;

    private long highThreats;

    private long mediumThreats;

    private long lowThreats;
}
