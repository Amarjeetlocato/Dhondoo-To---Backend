package com.whoami.businessoperation.dto.response;

import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.domain.enums.DocumentType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessDocumentResponse {

    private UUID id;

    private UUID businessId;

    private UUID applicationId;

    private DocumentType documentType;

    private String documentUrl;

    private String documentName;

    private String documentNumber;

    private DocumentStatus documentStatus;

    private LocalDate expiryDate;

    private String rejectionReason;

    private LocalDateTime submittedAt;

    private LocalDateTime reviewedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}