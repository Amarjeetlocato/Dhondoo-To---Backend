package com.whoami.launch.dto;

import java.time.LocalDateTime;

import com.whoami.launch.enums.ReportReason;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;

import lombok.Data;

@Data
public class ReportResponse {

    private Long id;

    private String reportedBy;

    private ReportTargetType targetType;

    private String targetId;

    private ReportReason reason;

    private String description;

    private ReportStatus status;

    private LocalDateTime createdAt;

    // Getters & Setters
}