package com.whoami.businessoperation.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.BusinessEvent;
import com.locato.constants.events.BusinessEventType;
import com.whoami.businessoperation.domain.entity.BusinessCapability;
import com.whoami.businessoperation.domain.enums.AuditAction;
import com.whoami.businessoperation.domain.enums.CapabilityStatus;
import com.whoami.businessoperation.domain.enums.CapabilityType;
import com.whoami.businessoperation.dto.request.UpdateBusinessCapabilityRequest;
import com.whoami.businessoperation.dto.response.BusinessCapabilityResponse;
import com.whoami.businessoperation.kafka.BusinessEventProducer;
import com.whoami.businessoperation.repository.BusinessCapabilityRepository;
import com.whoami.businessoperation.service.BusinessAuditService;
import com.whoami.businessoperation.service.BusinessCapabilityService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class BusinessCapabilityServiceImpl
        implements BusinessCapabilityService {

    private final BusinessCapabilityRepository capabilityRepository;
    private final BusinessAuditService businessAuditService;
    private final BusinessEventProducer businessEventProducer;

    @Override
    public BusinessCapabilityResponse enableCapability(
            UpdateBusinessCapabilityRequest request) {

        BusinessCapability capability =
                getOrCreateCapability(
                        request.getBusinessId(),
                        request.getCapabilityType()
                );

        capability.setCapabilityStatus(
                CapabilityStatus.ENABLED
        );

        capability.setEnabledBy(
                request.getPerformedBy()
        );

        capability.setDisabledBy(null);
        capability.setReason(request.getReason());
        capability.setEnabledAt(LocalDateTime.now());
        capability.setDisabledAt(null);

        BusinessCapability saved =
                capabilityRepository.save(capability);

        businessAuditService.log(
                saved.getBusinessId(),
                null,
                AuditAction.CAPABILITY_ENABLED,
                request.getPerformedBy(),
                "ADMIN",
                request.getReason(),
                null,
                null
        );

        publishEvent(
                BusinessEventType.CAPABILITY_ENABLED,
                saved,
                request.getPerformedBy(),
                "ADMIN",
                "Business capability enabled",
                request.getReason()
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessCapabilityResponse disableCapability(
            UpdateBusinessCapabilityRequest request) {

        BusinessCapability capability =
                getCapabilityEntity(
                        request.getBusinessId(),
                        request.getCapabilityType()
                );

        capability.setCapabilityStatus(
                CapabilityStatus.DISABLED
        );

        capability.setDisabledBy(
                request.getPerformedBy()
        );

        capability.setReason(request.getReason());
        capability.setDisabledAt(LocalDateTime.now());

        BusinessCapability saved =
                capabilityRepository.save(capability);

        businessAuditService.log(
                saved.getBusinessId(),
                null,
                AuditAction.CAPABILITY_DISABLED,
                request.getPerformedBy(),
                "ADMIN",
                request.getReason(),
                null,
                null
        );

        publishEvent(
                BusinessEventType.CAPABILITY_DISABLED,
                saved,
                request.getPerformedBy(),
                "ADMIN",
                "Business capability disabled",
                request.getReason()
        );

        return mapToResponse(saved);
    }

    @Override
    public BusinessCapabilityResponse suspendCapability(
            UpdateBusinessCapabilityRequest request) {

        BusinessCapability capability =
                getCapabilityEntity(
                        request.getBusinessId(),
                        request.getCapabilityType()
                );

        capability.setCapabilityStatus(
                CapabilityStatus.SUSPENDED
        );

        capability.setDisabledBy(
                request.getPerformedBy()
        );

        capability.setReason(request.getReason());
        capability.setDisabledAt(LocalDateTime.now());

        BusinessCapability saved =
                capabilityRepository.save(capability);

        businessAuditService.log(
                saved.getBusinessId(),
                null,
                AuditAction.SYSTEM_ACTION,
                request.getPerformedBy(),
                "ADMIN",
                "Business capability suspended: "
                        + request.getReason(),
                null,
                null
        );

        publishEvent(
                BusinessEventType.CAPABILITY_SUSPENDED,
                saved,
                request.getPerformedBy(),
                "ADMIN",
                "Business capability suspended",
                request.getReason()
        );

        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public BusinessCapabilityResponse getCapability(
            UUID businessId,
            CapabilityType capabilityType) {

        return mapToResponse(
                getCapabilityEntity(
                        businessId,
                        capabilityType
                )
        );
    }

   
    private BusinessCapability getCapabilityEntity(
            UUID businessId,
            CapabilityType capabilityType) {

        return capabilityRepository
                .findByBusinessIdAndCapabilityType(
                        businessId,
                        capabilityType
                )
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Capability not found for businessId: "
                                        + businessId
                                        + " and capabilityType: "
                                        + capabilityType
                        )
                );
    }
   
    @Override
    @Transactional(readOnly = true)
    public List<BusinessCapabilityResponse> getBusinessCapabilities(
            UUID businessId) {

        return capabilityRepository
                .findByBusinessId(businessId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }
    
  
    private void publishEvent(
            BusinessEventType eventType,
            BusinessCapability capability,
            UUID performedBy,
            String performedByRole,
            String description,
            String reason) {

        BusinessEvent event =
                BusinessEvent.builder()
                        .eventId(UUID.randomUUID())
                        .eventType(eventType)
                        .businessId(
                                capability.getBusinessId()
                        )
                        .performedBy(performedBy)
                        .performedByRole(performedByRole)
                        .description(description)
                        .reason(reason)
                        .occurredAt(LocalDateTime.now())
                        .build();

        businessEventProducer.publishCapabilityEvent(event);
    }

    private BusinessCapability getOrCreateCapability(
            UUID businessId,
            CapabilityType capabilityType) {

        return capabilityRepository
                .findByBusinessIdAndCapabilityType(
                        businessId,
                        capabilityType
                )
                .orElseGet(() -> {

                    BusinessCapability capability =
                            new BusinessCapability();

                    capability.setBusinessId(
                            businessId
                    );

                    capability.setCapabilityType(
                            capabilityType
                    );

                    capability.setCapabilityStatus(
                            CapabilityStatus.DISABLED
                    );

                    return capability;
                });
    }
    
    private BusinessCapabilityResponse mapToResponse(
            BusinessCapability capability) {

        BusinessCapabilityResponse response =
                new BusinessCapabilityResponse();

        response.setId(capability.getId());
        response.setBusinessId(capability.getBusinessId());
        response.setCapabilityType(capability.getCapabilityType());
        response.setCapabilityStatus(capability.getCapabilityStatus());
        response.setEnabledBy(capability.getEnabledBy());
        response.setDisabledBy(capability.getDisabledBy());
        response.setReason(capability.getReason());
        response.setEnabledAt(capability.getEnabledAt());
        response.setDisabledAt(capability.getDisabledAt());
        response.setCreatedAt(capability.getCreatedAt());
        response.setUpdatedAt(capability.getUpdatedAt());

        return response;
    }
}