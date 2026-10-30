package com.whoami.launch.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.locato.constants.events.service.ServiceCreatedEvent;
import com.locato.constants.events.service.ServiceDeletedEvent;
import com.locato.constants.events.service.ServiceUpdatedEvent;
import com.whoami.launch.config.MediaValidationService;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.entity.Service;
import com.whoami.launch.enums.BusinessCapability;
import com.whoami.launch.exception.MediaValidationException;
import com.whoami.launch.producer.ServiceEventProducer;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.BusinessAccessService;
import com.whoami.launch.service.ServiceService;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final MediaValidationService mediaValidationService;
    private final BusinessRepository businessRepository;
    private final BusinessAccessService businessAccessService;
    private final BusinessAccessStateRepository accessStateRepository;
    private final ServiceEventProducer serviceEventProducer;

    // ==================== CREATE SERVICE ====================

    @Override
    public Service createService(Service service) {
        if (service == null) {
            service = new Service();
        }

        if (service.getServiceName() == null || service.getServiceName().isBlank()) {
            service.setServiceName("New Service " + (System.currentTimeMillis() % 1000));
        }

        if (service.getPrice() == null) {
            service.setPrice(0.0);
        }

        if (service.getBusinessId() != null && !service.getBusinessId().isBlank()) {
            long currentServiceCount = serviceRepository.countByBusinessId(service.getBusinessId());
            businessAccessService.validateCreationAccess(
                    service.getBusinessId(),
                    BusinessCapability.SERVICE,
                    currentServiceCount);
        }

        try {
            if (service.getThumbnailPublicId() != null) {
                mediaValidationService.validateThumbnail(
                        service.getThumbnailPublicId());
            }

            if (service.getVideoPublicId() != null) {
                mediaValidationService.validateVideo(
                        service.getVideoPublicId());
            }
        } catch (Exception ignored) {
        }

        Service savedService = serviceRepository.save(service);

        try {
            if (savedService.getBusinessId() != null) {
                businessRepository.findByBusinessId(savedService.getBusinessId())
                        .ifPresent(business ->
                                serviceEventProducer.publishServiceCreated(
                                        ServiceCreatedEvent.builder()
                                                .serviceId(savedService.getServiceId())
                                                .serviceName(savedService.getServiceName())
                                                .serviceThumbnail(savedService.getThumbnailUrl())
                                                .serviceDescription(savedService.getServiceDescription())
                                                .price(savedService.getPrice())
                                                .businessId(business.getBusinessId())
                                                .businessName(business.getBusinessName())
                                                .businessLogo(null)
                                                .businessBanner(null)
                                                .userId(business.getUserId())
                                                .build()
                                )
                        );
            }
        } catch (Exception ignored) {
        }

        return savedService;
    }

    // ==================== GET ALL SERVICES - PAGINATION ====================

    @Override
    public PageResponse<ServiceResponseDTO> getAllservices(
            Pageable pageable) {

        Page<Service> page = serviceRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent()
                        .stream()
                        .map(this::toResponseDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    // ==================== UPDATE SERVICE ====================

    @Override
    public Service updateService(
            String serviceId,
            Service serviceDetails) {

        Service existingService = serviceRepository
                .findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException(
                        "Service not found with id: " + serviceId));

        Business business = businessRepository
                .findByBusinessId(existingService.getBusinessId())
                .orElseThrow(() -> new RuntimeException(
                        "Business not found for service: "
                                + existingService.getServiceId()));

        BusinessAccessState accessState = accessStateRepository
                .findByBusinessId(existingService.getBusinessId())
                .orElseGet(() -> {
                    BusinessAccessState state = new BusinessAccessState();
                    state.setBusinessId(existingService.getBusinessId());
                    return state;
                });

        boolean isSubscriptionActive = accessState.isSubscriptionActive();

        if (!isSubscriptionActive) {
            boolean attemptingRestrictedChanges =
                    (serviceDetails.getServiceName() != null && !serviceDetails.getServiceName().equals(existingService.getServiceName())) ||
                    (serviceDetails.getServiceDescription() != null && !serviceDetails.getServiceDescription().equals(existingService.getServiceDescription())) ||
                    (serviceDetails.getOrderType() != null && !serviceDetails.getOrderType().equals(existingService.getOrderType())) ||
                    (serviceDetails.getSuggestion() != null && !serviceDetails.getSuggestion().equals(existingService.getSuggestion())) ||
                    (serviceDetails.getVisibility() != null && !serviceDetails.getVisibility().equals(existingService.getVisibility())) ||
                    (serviceDetails.getBadges() != null && !serviceDetails.getBadges().equals(existingService.getBadges())) ||
                    (serviceDetails.getThumbnailUrl() != null && !serviceDetails.getThumbnailUrl().equals(existingService.getThumbnailUrl())) ||
                    (serviceDetails.getPromoVideoUrl() != null && !serviceDetails.getPromoVideoUrl().equals(existingService.getPromoVideoUrl()));

            if (attemptingRestrictedChanges) {
                throw new IllegalStateException(
                        "Without an active subscription, only service price and duration can be updated."
                );
            }
        }

        List<String> changes = new ArrayList<>();

        if (serviceDetails.getServiceName() != null) {
            existingService.setServiceName(
                    serviceDetails.getServiceName());
            changes.add("serviceName");
        }

        if (serviceDetails.getServiceDescription() != null) {
            existingService.setServiceDescription(
                    serviceDetails.getServiceDescription());
            changes.add("serviceDescription");
        }

        if (serviceDetails.getPrice() != null) {
            existingService.setPrice(serviceDetails.getPrice());
            changes.add("price");
        }

        if (serviceDetails.getDuration() != null) {
            existingService.setDuration(serviceDetails.getDuration());
            changes.add("duration");
        }

        if (serviceDetails.getOrderType() != null) {
            existingService.setOrderType(serviceDetails.getOrderType());
            changes.add("orderType");
        }

        if (serviceDetails.getSuggestion() != null) {
            existingService.setSuggestion(serviceDetails.getSuggestion());
            changes.add("suggestion");
        }

        if (serviceDetails.getVisibility() != null) {
            existingService.setVisibility(serviceDetails.getVisibility());
            changes.add("visibility");
        }

        if (serviceDetails.getBadges() != null) {
            existingService.setBadges(serviceDetails.getBadges());
            changes.add("badges");
        }

        // Validate and update thumbnail
        if (serviceDetails.getThumbnailUrl() != null
                && serviceDetails.getThumbnailPublicId() != null) {

            try {
                mediaValidationService.validateThumbnail(
                        serviceDetails.getThumbnailPublicId());
            } catch (Exception e) {
                throw new MediaValidationException(
                        "Thumbnail validation failed: " + e.getMessage());
            }

            existingService.setThumbnailUrl(
                    serviceDetails.getThumbnailUrl());

            existingService.setThumbnailPublicId(
                    serviceDetails.getThumbnailPublicId());

            changes.add("thumbnail");
        }

        // Validate and update promotional video
        if (serviceDetails.getPromoVideoUrl() != null
                && serviceDetails.getVideoPublicId() != null) {

            try {
                mediaValidationService.validateVideo(
                        serviceDetails.getVideoPublicId());
            } catch (Exception e) {
                throw new MediaValidationException(
                        "Video validation failed: " + e.getMessage());
            }

            existingService.setPromoVideoUrl(
                    serviceDetails.getPromoVideoUrl());

            existingService.setVideoPublicId(
                    serviceDetails.getVideoPublicId());

            changes.add("promoVideo");
        }

        Service updatedService =
                serviceRepository.save(existingService);

        // Publish only when at least one field was updated.
        if (!changes.isEmpty()) {
            serviceEventProducer.publishServiceUpdated(
                    ServiceUpdatedEvent.builder()
                            .serviceId(updatedService.getServiceId())
                            .serviceName(updatedService.getServiceName())
                            .serviceThumbnail(
                                    updatedService.getThumbnailUrl())
                            .serviceDescription(
                                    updatedService.getServiceDescription())
                            .price(updatedService.getPrice())
                            .businessId(business.getBusinessId())
                            .businessName(business.getBusinessName())
                            .businessLogo(null)
                            .businessBanner(null)
                            .userId(business.getUserId())
                            .changes(String.join(",", changes))
                            .build());
        }

        return updatedService;
    }

    // ==================== GET SERVICE BY ID ====================

    @Override
    public Optional<Service> getServiceById(String serviceId) {
        return serviceRepository.findByServiceId(serviceId);
    }

    // ==================== GET SERVICES BY NAME ====================

    @Override
    public List<Service> getServicesByName(String serviceName) {
        return serviceRepository.findByServiceName(serviceName);
    }

    // ==================== SEARCH SERVICES ====================

    @Override
    public List<Service> searchServices(String serviceName) {
        return serviceRepository
                .findByServiceNameContaining(serviceName);
    }

    // ==================== GET SERVICES BY BUSINESS ====================

    @Override
    public List<Service> getServicesByBusinessId(String businessId) {
        return serviceRepository.findByBusinessId(businessId);
    }

    // ==================== GET SERVICES BY VISIBILITY ====================

    @Override
    public List<Service> getServicesByVisibility(String visibility) {
        return serviceRepository.findByVisibility(visibility);
    }

    // ==================== GET SERVICES BY BADGES ====================

    @Override
    public List<Service> getServicesByBadges(String badges) {
        return serviceRepository.findByBadges(badges);
    }

    // ==================== GET ALL SERVICES ====================

    @Override
    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    // ==================== DELETE SERVICE ====================

    @Override
    public void deleteService(String serviceId) {

        Service service = serviceRepository
                .findByServiceId(serviceId)
                .orElseThrow(() -> new RuntimeException(
                        "Service not found with id: " + serviceId));

        Business business = businessRepository
                .findByBusinessId(service.getBusinessId())
                .orElseThrow(() -> new RuntimeException(
                        "Business not found for service: "
                                + service.getServiceId()));

        // Publish deletion event before deleting the database record.
        serviceEventProducer.publishServiceDeleted(
                ServiceDeletedEvent.builder()
                        .serviceId(service.getServiceId())
                        .serviceName(service.getServiceName())
                        .serviceThumbnail(service.getThumbnailUrl())
                        .businessId(business.getBusinessId())
                        .businessName(business.getBusinessName())
                        .businessLogo(null)
                        .businessBanner(null)
                        .userId(business.getUserId())
                        .build());

        serviceRepository.delete(service);
    }

    // ==================== RESPONSE DTO ====================

    @Override
    public ServiceResponseDTO toResponseDTO(Service service) {

        if (service == null) {
            return null;
        }

        String businessName = getBusinessName(service.getBusinessId());

        return new ServiceResponseDTO(
                service.getServiceId(),
                service.getBusinessId(),
                businessName,
                service.getServiceName(),
                service.getPrice(),
                service.getServiceDescription(),
                service.getDuration(),
                service.getOrderType(),
                service.getSuggestion(),
                service.getVisibility(),
                service.getBadges(),
                service.getThumbnailUrl(),
                service.getThumbnailPublicId(),
                service.getPromoVideoUrl(),
                service.getVideoPublicId());
    }

    // ==================== SUMMARY DTO ====================

    @Override
    public ServiceSummaryDTO toSummaryDTO(Service service) {

        if (service == null) {
            return null;
        }

        String businessName = getBusinessName(service.getBusinessId());

        return new ServiceSummaryDTO(
                service.getServiceId(),
                service.getServiceName(),
                service.getBusinessId(),
                businessName,
                service.getPrice(),
                service.getServiceDescription());
    }

    // ==================== BUSINESS NAME ====================

    private String getBusinessName(String businessId) {

        return businessRepository
                .findByBusinessId(businessId)
                .map(Business::getBusinessName)
                .orElse(null);
    }
}