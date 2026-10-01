package com.whoami.launch.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ReportRequest;
import com.whoami.launch.dto.ReportResponse;
import com.whoami.launch.entity.Report;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ReportRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.ReportService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final ReelRepository reelRepository;

    @Override
    public ReportResponse createReport(
            String userId,
            ReportRequest request) {

        validateTarget(
                request.getTargetType(),
                request.getTargetId());

        if (reportRepository.existsByUserIdAndTargetTypeAndTargetId(
                userId,
                request.getTargetType(),
                request.getTargetId())) {

            throw new RuntimeException(
                    "You already reported this item");
        }

        Report report = new Report();

        report.setUserId(userId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);

        return mapToResponse(
                reportRepository.save(report));
    }

    @Override
    public PageResponse<ReportResponse> getReports(
            int page,
            int size) {

        Page<Report> reportPage =
                reportRepository.findByStatus(
                        ReportStatus.PENDING,
                        PageRequest.of(page, size));

        List<ReportResponse> content =
                reportPage.getContent()
                        .stream()
                        .map(this::mapToResponse)
                        .toList();

        return new PageResponse<>(
                content,
                reportPage.getNumber(),
                reportPage.getSize(),
                reportPage.getTotalElements(),
                reportPage.getTotalPages(),
                reportPage.isFirst(),
                reportPage.isLast()
        );
    }

    @Override
    public ReportResponse updateStatus(
            String reportId,
            ReportStatus status) {

        Report report =
                reportRepository.findByReportId(reportId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Report not found"));

        report.setStatus(status);

        return mapToResponse(
                reportRepository.save(report));
    }

    private void validateTarget(
            ReportTargetType targetType,
            String targetId) {

        switch (targetType) {

            case PRODUCT:
                productRepository.findByProductId(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Product not found"));
                break;

            case SERVICE:
                serviceRepository.findByServiceId(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service not found"));
                break;

            case REEL:
                reelRepository.findByReelId(targetId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Reel not found"));
                break;

            default:
                throw new RuntimeException(
                        "Invalid report target type");
        }
    }

    private ReportResponse mapToResponse(
            Report report) {

        ReportResponse response =
                new ReportResponse();

        response.setReportId(report.getReportId());
        response.setUserId(report.getUserId());
        response.setTargetType(report.getTargetType());
        response.setTargetId(report.getTargetId());
        response.setReason(report.getReason());
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus());
        response.setCreatedAt(report.getCreatedAt());

        return response;
    }
}