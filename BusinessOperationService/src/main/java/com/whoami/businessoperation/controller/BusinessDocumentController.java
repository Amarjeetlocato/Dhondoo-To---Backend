package com.whoami.businessoperation.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.dto.request.UploadBusinessDocumentRequest;
import com.whoami.businessoperation.dto.response.BusinessDocumentResponse;
import com.whoami.businessoperation.service.BusinessDocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/business-operations/documents")
@RequiredArgsConstructor
public class BusinessDocumentController {

    private final BusinessDocumentService businessDocumentService;

    @PostMapping
    public ResponseEntity<BusinessDocumentResponse> uploadDocument(
            @Valid @RequestBody UploadBusinessDocumentRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        businessDocumentService.uploadDocument(request)
                );
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<BusinessDocumentResponse> getDocument(
            @PathVariable String documentId) {

        return ResponseEntity.ok(
                businessDocumentService.getDocument(documentId)
        );
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessDocumentResponse>>
    getBusinessDocuments(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                businessDocumentService
                        .getBusinessDocuments(businessId)
        );
    }

    @PutMapping("/{documentId}/status")
    public ResponseEntity<BusinessDocumentResponse>
    updateDocumentStatus(
            @PathVariable String documentId,
            @RequestParam DocumentStatus status,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam String reviewedBy) {

        return ResponseEntity.ok(
                businessDocumentService.updateDocumentStatus(
                        documentId,
                        status,
                        rejectionReason,
                        reviewedBy
                )
        );
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable String documentId) {

        businessDocumentService.deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}