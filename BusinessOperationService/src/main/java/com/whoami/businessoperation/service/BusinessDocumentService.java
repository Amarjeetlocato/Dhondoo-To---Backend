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
    		String documentId
    );

    List<BusinessDocumentResponse> getBusinessDocuments(
    		String businessId
    );

    BusinessDocumentResponse updateDocumentStatus(
    		String documentId,
            DocumentStatus status,
            String rejectionReason,
            String reviewedBy
    );

    void deleteDocument(
    		String documentId
    );
}