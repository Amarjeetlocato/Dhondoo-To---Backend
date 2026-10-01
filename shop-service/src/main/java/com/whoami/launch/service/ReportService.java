package com.whoami.launch.service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReportRequest;
import com.whoami.launch.dto.ReportResponse;
import com.whoami.launch.enums.ReportStatus;

public interface ReportService {

    ReportResponse createReport(
            String userId,
            ReportRequest request);

    PageResponse<ReportResponse> getReports(
            int page,
            int size);

    ReportResponse updateStatus(
            String reportId,
            ReportStatus status);
}
