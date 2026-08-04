package com.whoami.launch.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.whoami.launch.repository.ShopRepository;
import com.whoami.launch.service.ReportService;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private ReelRepository reelRepository;

    
    public ReportResponse createReport(
            String userId,
            ReportRequest request) {

        validateTarget(
                request.getTargetType(),
                request.getTargetId());

        if (reportRepository.existsByReportedByAndTargetTypeAndTargetId(
                userId,
                request.getTargetType(),
                request.getTargetId())) {

            throw new RuntimeException(
                    "You already reported this item");
        }

        Report report = new Report();

        report.setReportedBy(userId);
        report.setTargetType(request.getTargetType());
        report.setTargetId(request.getTargetId());
        report.setReason(request.getReason());
        report.setDescription(request.getDescription());
        report.setStatus(ReportStatus.PENDING);

        return mapToResponse(
                reportRepository.save(report));
    }

    
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
                        .collect(Collectors.toList());

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

    
    public ReportResponse updateStatus(
            Long reportId,
            ReportStatus status) {

        Report report = reportRepository.findById(reportId)
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
                productRepository.findById(targetId)
                        .orElseThrow(() ->
                                new RuntimeException("Product not found"));
                break;

            case SERVICE:
                serviceRepository.findById(targetId)
                        .orElseThrow(() ->
                                new RuntimeException("Service not found"));
                break;

            case SHOP:
                shopRepository.findById(targetId)
                        .orElseThrow(() ->
                                new RuntimeException("Shop not found"));
                break;

            case REEL:
                reelRepository.findById(targetId)
                        .orElseThrow(() ->
                                new RuntimeException("Reel not found"));
                break;

            default:
                break;
        }
    }

    private ReportResponse mapToResponse(
            Report report) {

        ReportResponse response =
                new ReportResponse();

        response.setId(report.getId());
        response.setReportedBy(report.getReportedBy());
        response.setTargetType(report.getTargetType());
        response.setTargetId(report.getTargetId());
        response.setReason(report.getReason());
        response.setDescription(report.getDescription());
        response.setStatus(report.getStatus());
        response.setCreatedAt(report.getCreatedAt());

        return response;
    }
}