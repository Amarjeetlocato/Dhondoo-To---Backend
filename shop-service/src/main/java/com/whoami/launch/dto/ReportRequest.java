package com.whoami.launch.dto;

import com.whoami.launch.enums.ReportReason;
import com.whoami.launch.enums.ReportTargetType;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReportRequest {

    @NotNull(message = "Target type is required")
    private ReportTargetType targetType;

    @NotNull(message = "Target id is required")
    private String targetId;

    @NotNull(message = "Reason is required")
    private ReportReason reason;

    @Size(max = 1000)
    private String description;

    // Getters & Setters
}