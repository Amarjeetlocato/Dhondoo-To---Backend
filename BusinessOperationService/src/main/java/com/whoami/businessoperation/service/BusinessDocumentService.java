package com.whoami.businessoperation.service;

import com.whoami.businessoperation.dto.request.UploadBusinessDocumentRequest;
import com.whoami.businessoperation.dto.response.BusinessDocumentResponse;
import com.whoami.businessoperation.domain.enums.DocumentStatus;

import java.util.List;
import java.util.UUID;

public interface BusinessDocumentService {

    BusinessDocumentResponse uploadDocument(
            UploadBusinessDocumentRequest request
    );

    BusinessDocumentResponse getDocument(
            UUID documentId
    );

    List<BusinessDocumentResponse> getBusinessDocuments(
            UUID businessId
    );

    BusinessDocumentResponse updateDocumentStatus(
            UUID documentId,
            DocumentStatus status,
            String rejectionReason,
            UUID reviewedBy
    );

    void deleteDocument(
            UUID documentId
    );
}