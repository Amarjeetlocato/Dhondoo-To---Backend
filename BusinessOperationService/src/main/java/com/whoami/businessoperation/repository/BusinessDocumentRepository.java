package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessDocument;
import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessDocumentRepository
        extends JpaRepository<BusinessDocument, UUID> {

    List<BusinessDocument> findByBusinessId(UUID businessId);

    List<BusinessDocument> findByApplicationId(UUID applicationId);

    List<BusinessDocument> findByBusinessIdAndDocumentStatus(
            UUID businessId,
            DocumentStatus documentStatus
    );

    Optional<BusinessDocument> findByBusinessIdAndDocumentType(
            UUID businessId,
            DocumentType documentType
    );

    boolean existsByBusinessIdAndDocumentType(
            UUID businessId,
            DocumentType documentType
    );
}