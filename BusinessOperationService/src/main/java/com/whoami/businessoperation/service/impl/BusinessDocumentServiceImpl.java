package com.whoami.businessoperation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.BusinessEvent;
import com.locato.constants.events.BusinessEventType;
import com.whoami.businessoperation.domain.entity.BusinessDocument;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.dto.request.UploadBusinessDocumentRequest;
import com.whoami.businessoperation.dto.response.BusinessDocumentResponse;
import com.whoami.businessoperation.kafka.BusinessEventProducer;
import com.whoami.businessoperation.repository.BusinessDocumentRepository;
import com.whoami.businessoperation.service.BusinessAuditService;
import com.whoami.businessoperation.service.BusinessDocumentService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessDocumentServiceImpl implements BusinessDocumentService {

    private final BusinessDocumentRepository businessDocumentRepository;
    private final BusinessAuditService businessAuditService;
    private final BusinessEventProducer businessEventProducer;

    @Override
    public BusinessDocumentResponse uploadDocument(
            UploadBusinessDocumentRequest request) {

        BusinessDocument document = new BusinessDocument();

        document.setBusinessId(request.getBusinessId());
        document.setApplicationId(request.getApplicationId());
        document.setDocumentType(request.getDocumentType());
        document.setDocumentUrl(request.getDocumentUrl());
        document.setDocumentName(request.getDocumentName());
        document.setDocumentNumber(request.getDocumentNumber());
        document.setExpiryDate(request.getExpiryDate());
        document.setDocumentStatus(DocumentStatus.PENDING);
        document.setSubmittedAt(LocalDateTime.now());

        BusinessDocument saved =
                businessDocumentRepository.save(document);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.DOCUMENT_UPLOADED,
                null,
                "BUSINESS_OWNER",
                "Business document uploaded",
                null,
                null
        );

        publishEvent(
                BusinessEventType.DOCUMENT_UPLOADED,
                saved,
                null,
                "BUSINESS_OWNER",
                "Business document uploaded",
                null
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessDocumentResponse getDocument(UUID documentId) {

        BusinessDocument document =
                getDocumentEntity(documentId);

        return mapToResponse(document);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessDocumentResponse> getBusinessDocuments(
            UUID businessId) {

        return businessDocumentRepository
                .findByBusinessId(businessId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BusinessDocumentResponse updateDocumentStatus(
            UUID documentId,
            DocumentStatus status,
            String rejectionReason,
            UUID reviewedBy) {

        BusinessDocument document =
                getDocumentEntity(documentId);

        document.setDocumentStatus(status);
        document.setRejectionReason(rejectionReason);
        document.setReviewedAt(LocalDateTime.now());

        BusinessDocument saved =
                businessDocumentRepository.save(document);

        AuditAction auditAction;
        BusinessEventType eventType;
        String description;

        if (status == DocumentStatus.APPROVED) {

            auditAction = AuditAction.DOCUMENT_APPROVED;
            eventType = BusinessEventType.DOCUMENT_APPROVED;
            description = "Business document approved";

        } else if (status == DocumentStatus.REJECTED) {

            auditAction = AuditAction.DOCUMENT_REJECTED;
            eventType = BusinessEventType.DOCUMENT_REJECTED;
            description = "Business document rejected";

        } else if (status == DocumentStatus.REUPLOAD_REQUIRED) {

            auditAction = AuditAction.REUPLOAD_REQUESTED;
            eventType = BusinessEventType.DOCUMENT_REUPLOAD_REQUIRED;
            description = "Business document reupload required";

        } else {

            auditAction = AuditAction.DOCUMENT_UPDATED;
            eventType = null;
            description = "Document status updated to " + status;
        }

        String finalDescription =
                rejectionReason != null
                        ? rejectionReason
                        : description;

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                auditAction,
                reviewedBy,
                "ADMIN",
                finalDescription,
                null,
                null
        );

        if (eventType != null) {
            publishEvent(
                    eventType,
                    saved,
                    reviewedBy,
                    "ADMIN",
                    description,
                    rejectionReason
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public void deleteDocument(UUID documentId) {

        BusinessDocument document =
                getDocumentEntity(documentId);

        UUID businessId = document.getBusinessId();
        UUID applicationId = document.getApplicationId();

        businessDocumentRepository.delete(document);

        businessAuditService.log(
                businessId,
                applicationId,
                AuditAction.DOCUMENT_UPDATED,
                null,
                "BUSINESS_OWNER",
                "Business document deleted",
                null,
                null
        );
    }

    private void publishEvent(
            BusinessEventType eventType,
            BusinessDocument document,
            UUID performedBy,
            String performedByRole,
            String description,
            String reason) {

        BusinessEvent event = BusinessEvent.builder()
                .eventId(UUID.randomUUID())
                .eventType(eventType)
                .businessId(document.getBusinessId())
                .applicationId(document.getApplicationId())
                .performedBy(performedBy)
                .performedByRole(performedByRole)
                .description(description)
                .reason(reason)
                .occurredAt(LocalDateTime.now())
                .build();

        businessEventProducer.publishBusinessEvent(event);
    }

    private BusinessDocument getDocumentEntity(UUID documentId) {

        return businessDocumentRepository.findById(documentId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Business document not found: " + documentId
                        ));
    }

    private BusinessDocumentResponse mapToResponse(
            BusinessDocument document) {

        BusinessDocumentResponse response =
                new BusinessDocumentResponse();

        response.setId(document.getId());
        response.setBusinessId(document.getBusinessId());
        response.setApplicationId(document.getApplicationId());
        response.setDocumentType(document.getDocumentType());
        response.setDocumentUrl(document.getDocumentUrl());
        response.setDocumentName(document.getDocumentName());
        response.setDocumentNumber(document.getDocumentNumber());
        response.setDocumentStatus(document.getDocumentStatus());
        response.setExpiryDate(document.getExpiryDate());
        response.setRejectionReason(document.getRejectionReason());
        response.setSubmittedAt(document.getSubmittedAt());
        response.setReviewedAt(document.getReviewedAt());
        response.setCreatedAt(document.getCreatedAt());
        response.setUpdatedAt(document.getUpdatedAt());

        return response;
    }
}
