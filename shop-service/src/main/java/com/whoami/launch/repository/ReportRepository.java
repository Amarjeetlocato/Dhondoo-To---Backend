package com.whoami.launch.repository;

import com.whoami.launch.entity.Product;
import com.whoami.launch.entity.Report;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository
        extends JpaRepository<Report, Long> {

    Page<Report> findByTargetTypeAndTargetId(
            ReportTargetType targetType,
            String targetId,
            Pageable pageable);

    Page<Report> findByStatus(
            ReportStatus status,
            Pageable pageable);

    long countByTargetTypeAndTargetId(
            ReportTargetType targetType,
            String targetId);

    boolean existsByUserIdAndTargetTypeAndTargetId(
            String userId,
            ReportTargetType targetType,
            String targetId);

	Optional<Report> findByReportId(String reportId);
}