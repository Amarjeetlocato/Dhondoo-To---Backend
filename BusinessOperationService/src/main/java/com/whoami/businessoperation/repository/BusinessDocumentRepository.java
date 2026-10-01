package com.whoami.businessoperation.repository;

import com.whoami.businessoperation.domain.entity.BusinessDocument;
import com.whoami.businessoperation.domain.enums.DocumentStatus;
import com.whoami.businessoperation.domain.enums.DocumentType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BusinessDocumentRepository
        extends JpaRepository<BusinessDocument, Long> {

    List<BusinessDocument> findByBusinessId(String businessId);

    List<BusinessDocument> findByApplicationId(String applicationId);

    List<BusinessDocument> findByBusinessIdAndDocumentStatus(
    		String businessId,
            DocumentStatus documentStatus
    );

    Optional<BusinessDocument> findByBusinessIdAndDocumentType(
    		String businessId,
            DocumentType documentType
    );

    boolean existsByBusinessIdAndDocumentType(
    		String businessId,
            DocumentType documentType
    );

	Optional<BusinessDocument> findByDocumentId(String documentId);
}