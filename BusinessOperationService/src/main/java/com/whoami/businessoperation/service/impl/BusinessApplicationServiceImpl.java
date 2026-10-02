package com.whoami.businessoperation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.EventSources;
import com.locato.constants.events.EventVersions;
import com.locato.constants.events.businessoperation.ApplicationCreatedEvent;
import com.locato.constants.events.businessoperation.BusinessOperationEventType;
import com.whoami.businessoperation.domain.entity.BusinessApplication;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.enums.BusinessApplicationStatus;
import com.whoami.businessoperation.domain.enums.BusinessOperationalStatus;
import com.whoami.businessoperation.dto.request.CreateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.SubmitBusinessApplicationRequest;
import com.whoami.businessoperation.dto.request.UpdateBusinessApplicationRequest;
import com.whoami.businessoperation.dto.response.BusinessApplicationResponse;
import com.whoami.businessoperation.kafka.BusinessEventProducer;
import com.whoami.businessoperation.repository.BusinessApplicationRepository;
import com.whoami.businessoperation.service.BusinessApplicationService;
import com.whoami.businessoperation.service.BusinessAuditService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessApplicationServiceImpl
        implements BusinessApplicationService {

    private final BusinessApplicationRepository businessApplicationRepository;
    private final BusinessAuditService businessAuditService;
    private final BusinessEventProducer businessEventProducer;

    @Override
    public BusinessApplicationResponse createApplication(
            CreateBusinessApplicationRequest request) {

        if (businessApplicationRepository.existsByBusinessId(
                request.getBusinessId())) {

            throw new IllegalStateException(
                    "Business application already exists for businessId: "
                            + request.getBusinessId()
            );
        }

        BusinessApplication application =
                new BusinessApplication();

        application.setBusinessId(request.getBusinessId());
        application.setOwnerUserId(request.getOwnerUserId());
        application.setBusinessType(request.getBusinessType());
        application.setBusinessName(request.getBusinessName());
        application.setDescription(request.getDescription());
        application.setPhone(request.getPhone());
        application.setEmail(request.getEmail());
        application.setAddress(request.getAddress());
        application.setLatitude(request.getLatitude());
        application.setLongitude(request.getLongitude());

        application.setApplicationStatus(
                BusinessApplicationStatus.DRAFT
        );

        application.setOperationalStatus(
                BusinessOperationalStatus.CREATED
        );

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.APPLICATION_CREATED,
                saved.getOwnerUserId(),
                "BUSINESS_OWNER",
                "Business application created",
                null,
                null
        );

        publishApplicationCreatedEvent(saved);

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessApplicationResponse getApplication(
            String businessId) {

        BusinessApplication application =
                getApplicationEntity(businessId);

        return mapToResponse(application);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BusinessApplicationResponse> getApplicationsByOwner(
            String ownerUserId) {

        return businessApplicationRepository
                .findByOwnerUserId(ownerUserId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BusinessApplicationResponse updateApplication(
            String businessId,
            UpdateBusinessApplicationRequest request) {

        BusinessApplication application =
                getApplicationEntity(businessId);

        if (application.getApplicationStatus()
                != BusinessApplicationStatus.DRAFT
                && application.getApplicationStatus()
                != BusinessApplicationStatus.REUPLOAD_REQUIRED) {

            throw new IllegalStateException(
                    "Application cannot be updated in current status: "
                            + application.getApplicationStatus()
            );
        }

        if (request.getBusinessType() != null) {
            application.setBusinessType(request.getBusinessType());
        }

        if (request.getBusinessName() != null) {
            application.setBusinessName(request.getBusinessName());
        }

        if (request.getDescription() != null) {
            application.setDescription(request.getDescription());
        }

        if (request.getPhone() != null) {
            application.setPhone(request.getPhone());
        }

        if (request.getEmail() != null) {
            application.setEmail(request.getEmail());
        }

        if (request.getAddress() != null) {
            application.setAddress(request.getAddress());
        }

        if (request.getLatitude() != null) {
            application.setLatitude(request.getLatitude());
        }

        if (request.getLongitude() != null) {
            application.setLongitude(request.getLongitude());
        }

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.SYSTEM_ACTION,
                saved.getOwnerUserId(),
                "BUSINESS_OWNER",
                "Business application updated",
                null,
                null
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessApplicationResponse submitApplication(
            SubmitBusinessApplicationRequest request) {

        BusinessApplication application =
                getApplicationEntity(
                        request.getBusinessId()
                );

        if (application.getApplicationStatus()
                != BusinessApplicationStatus.DRAFT
                && application.getApplicationStatus()
                != BusinessApplicationStatus.REUPLOAD_REQUIRED) {

            throw new IllegalStateException(
                    "Application cannot be submitted in current status: "
                            + application.getApplicationStatus()
            );
        }

        application.setApplicationStatus(
                BusinessApplicationStatus.SUBMITTED
        );

        application.setSubmittedAt(
                LocalDateTime.now()
        );

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.APPLICATION_SUBMITTED,
                saved.getOwnerUserId(),
                "BUSINESS_OWNER",
                "Business application submitted",
                null,
                null
        );

        publishApplicationEvent(
                BusinessOperationEventType.APPLICATION_SUBMITTED,
                saved,
                saved.getOwnerUserId(),
                "BUSINESS_OWNER",
                "Business application submitted",
                null
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessApplicationResponse rejectApplication(
            String businessId,
            String reason,
            String performedBy) {

        BusinessApplication application =
                getApplicationEntity(businessId);

        if (application.getApplicationStatus()
                == BusinessApplicationStatus.REJECTED) {

            return mapToResponse(application);
        }

        application.setApplicationStatus(
                BusinessApplicationStatus.REJECTED
        );

        application.setRejectedAt(
                LocalDateTime.now()
        );

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.BUSINESS_REJECTED,
                performedBy,
                "ADMIN",
                reason,
                null,
                null
        );

        publishApplicationEvent(
                BusinessOperationEventType.BUSINESS_REJECTED,
                saved,
                performedBy,
                "ADMIN",
                "Business application rejected",
                reason
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessApplicationResponse approveApplication(
            String businessId,
            String performedBy) {

        BusinessApplication application =
                getApplicationEntity(businessId);

        /*
         * Idempotency guard.
         *
         * If the same VERIFICATION_APPROVED event is received again,
         * the application is already approved and activated.
         * Do not create duplicate audit/events.
         */
        if (application.getApplicationStatus()
                == BusinessApplicationStatus.APPROVED) {

            return mapToResponse(application);
        }

        /*
         * Approval is allowed only after the application has reached
         * a valid verification/review stage.
         */
        switch (application.getApplicationStatus()) {

            case SUBMITTED:
            case PAYMENT_PENDING:
            case UNDER_REVIEW:
            case DOCUMENT_VERIFICATION:
            case VIDEO_VERIFICATION:
            case VERIFICATION_PENDING:
            case REUPLOAD_REQUIRED:
                break;

            case DRAFT:
            case REJECTED:
            case CANCELLED:

                throw new IllegalStateException(
                        "Application cannot be approved in current status: "
                                + application.getApplicationStatus()
                );

            default:

                throw new IllegalStateException(
                        "Application cannot be approved in current status: "
                                + application.getApplicationStatus()
                );
        }

        application.setApplicationStatus(
                BusinessApplicationStatus.APPROVED
        );

        application.setOperationalStatus(
                BusinessOperationalStatus.ACTIVE
        );

        application.setApprovedAt(
                LocalDateTime.now()
        );

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.BUSINESS_APPROVED,
                performedBy,
                "ADMIN",
                "Business application approved",
                null,
                null
        );

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.BUSINESS_ACTIVATED,
                performedBy,
                "ADMIN",
                "Business activated after approval",
                null,
                null
        );

        publishApplicationEvent(
                BusinessOperationEventType.BUSINESS_APPROVED,
                saved,
                performedBy,
                "ADMIN",
                "Business application approved",
                null
        );

        publishApplicationEvent(
                BusinessOperationEventType.BUSINESS_ACTIVATED,
                saved,
                performedBy,
                "ADMIN",
                "Business activated after approval",
                null
        );

        return mapToResponse(saved);
    }

    
    @Override
    public BusinessApplicationResponse requestApplicationReupload(
            String businessId,
            String reason,
            String performedBy) {

        BusinessApplication application =
                getApplicationEntity(businessId);

        if (application.getApplicationStatus()
                == BusinessApplicationStatus.APPROVED) {

            throw new IllegalStateException(
                    "Approved application cannot be sent for reupload: "
                            + businessId
            );
        }

        application.setApplicationStatus(
                BusinessApplicationStatus.REUPLOAD_REQUIRED
        );

        BusinessApplication saved =
                businessApplicationRepository.save(application);

        businessAuditService.log(
                saved.getBusinessId(),
                saved.getApplicationId(),
                AuditAction.REUPLOAD_REQUESTED,
                performedBy,
                "ADMIN",
                reason != null
                        ? reason
                        : "Business application reupload required",
                null,
                null
        );

        return mapToResponse(saved);
    }
    
    private void publishApplicationCreatedEvent(
            BusinessApplication application) {

        ApplicationCreatedEvent event =
                ApplicationCreatedEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(
                                BusinessOperationEventType.APPLICATION_CREATED
                        )
                        .eventVersion(EventVersions.V1)
                        .source(EventSources.BUSINESS_OPERATION_SERVICE)
                        .occurredAt(LocalDateTime.now())
                        .correlationId(null)
                        .businessId(application.getBusinessId())
                        .applicationId(application.getApplicationId())
                        .userId(application.getOwnerUserId())
                        .description(
                                "Business application created"
                        )
                        .build();

        businessEventProducer.publishApplicationCreatedEvent(event);
    }

    private void publishApplicationEvent(
            BusinessOperationEventType eventType,
            BusinessApplication application,
            String performedBy,
            String performedByRole,
            String description,
            String reason) {

        businessEventProducer.publishBusinessOperationEvent(
                eventType,
                application,
                performedBy,
                performedByRole,
                description,
                reason
        );
    }

    private BusinessApplication getApplicationEntity(
            String businessId) {

        return businessApplicationRepository
                .findByBusinessId(businessId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Business application not found for businessId: "
                                        + businessId
                        ));
    }

    private BusinessApplicationResponse mapToResponse(
            BusinessApplication application) {

        BusinessApplicationResponse response =
                new BusinessApplicationResponse();

        response.setApplicationId(
                application.getApplicationId()
        );

        response.setBusinessId(
                application.getBusinessId()
        );

        response.setOwnerUserId(
                application.getOwnerUserId()
        );

        response.setBusinessType(
                application.getBusinessType()
        );

        response.setBusinessName(
                application.getBusinessName()
        );

        response.setDescription(
                application.getDescription()
        );

        response.setPhone(
                application.getPhone()
        );

        response.setEmail(
                application.getEmail()
        );

        response.setAddress(
                application.getAddress()
        );

        response.setLatitude(
                application.getLatitude()
        );

        response.setLongitude(
                application.getLongitude()
        );

        response.setApplicationStatus(
                application.getApplicationStatus()
        );

        response.setOperationalStatus(
                application.getOperationalStatus()
        );

        response.setSubmittedAt(
                application.getSubmittedAt()
        );

        response.setApprovedAt(
                application.getApprovedAt()
        );

        response.setRejectedAt(
                application.getRejectedAt()
        );

        response.setSuspendedAt(
                application.getSuspendedAt()
        );

        response.setCreatedAt(
                application.getCreatedAt()
        );

        response.setUpdatedAt(
                application.getUpdatedAt()
        );

        return response;
    }
}