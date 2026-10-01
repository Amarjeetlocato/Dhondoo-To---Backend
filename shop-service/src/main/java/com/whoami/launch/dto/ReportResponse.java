package com.whoami.launch.dto;

import com.whoami.launch.enums.ReportReason;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportResponse {

    private String reportId;

    private String userId;

    private ReportTargetType targetType;
    private String targetId;

    private ReportReason reason;
    private String description;

    private ReportStatus status;

    private LocalDateTime createdAt;
}
