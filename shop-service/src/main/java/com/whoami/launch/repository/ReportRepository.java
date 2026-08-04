package com.whoami.launch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.whoami.launch.entity.Report;
import com.whoami.launch.enums.ReportStatus;
import com.whoami.launch.enums.ReportTargetType;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {

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

    boolean existsByReportedByAndTargetTypeAndTargetId(
    		String reportedBy,
            ReportTargetType targetType,
            String targetId);
}