package com.whoami.businessoperation.dto.request;

import com.whoami.businessoperation.domain.enums.DocumentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadBusinessDocumentRequest {

    @NotNull(message = "Business ID is required")
    private UUID businessId;

    @NotNull(message = "Application ID is required")
    private UUID applicationId;

    @NotNull(message = "Document type is required")
    private DocumentType documentType;

    @NotBlank(message = "Document URL is required")
    private String documentUrl;

    private String documentName;

    private String documentNumber;

    private LocalDate expiryDate;
}