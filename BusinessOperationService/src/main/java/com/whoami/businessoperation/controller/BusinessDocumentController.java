package com.whoami.businessoperation.controller;

import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.dto.request.UploadBusinessDocumentRequest;
import com.whoami.businessoperation.dto.response.BusinessDocumentResponse;
import com.whoami.businessoperation.service.BusinessDocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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
            @PathVariable UUID documentId) {

        return ResponseEntity.ok(
                businessDocumentService.getDocument(documentId)
        );
    }

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<BusinessDocumentResponse>>
    getBusinessDocuments(
            @PathVariable UUID businessId) {

        return ResponseEntity.ok(
                businessDocumentService
                        .getBusinessDocuments(businessId)
        );
    }

    @PutMapping("/{documentId}/status")
    public ResponseEntity<BusinessDocumentResponse>
    updateDocumentStatus(
            @PathVariable UUID documentId,
            @RequestParam DocumentStatus status,
            @RequestParam(required = false) String rejectionReason,
            @RequestParam UUID reviewedBy) {

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
            @PathVariable UUID documentId) {

        businessDocumentService.deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}