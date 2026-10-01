package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.whoami.launch.entity.Service;
import com.whoami.launch.config.MediaValidationService;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.exception.MediaValidationException;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.ServiceService;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceServiceImpl implements ServiceService {

    private final ServiceRepository serviceRepository;
    private final MediaValidationService mediaValidationService;
    private final BusinessRepository businessRepository;

    @Override
    public Service createService(Service service) {

        businessRepository.findByBusinessId(service.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found with id: "
                                        + service.getBusinessId()));

        try {

            if (service.getThumbnailPublicId() != null) {
                mediaValidationService.validateThumbnail(
                        service.getThumbnailPublicId());
            }

            if (service.getVideoPublicId() != null) {
                mediaValidationService.validateVideo(
                        service.getVideoPublicId());
            }

        } catch (Exception e) {

            throw new MediaValidationException(
                    "Media validation failed: "
                            + e.getMessage());
        }

        return serviceRepository.save(service);
    }

    @Override
    public PageResponse<ServiceResponseDTO> getAllservices(
            Pageable pageable) {

        Page<Service> page =
                serviceRepository.findAll(pageable);

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
                page.isLast()
        );
    }

    @Override
    public Service updateService(
            String serviceId,
            Service serviceDetails) {

        Service existingService =
                serviceRepository.findByServiceId(serviceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service not found with id: "
                                                + serviceId));

        businessRepository.findByBusinessId(
                        existingService.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found for service: "
                                        + existingService.getServiceId()));

        if (serviceDetails.getServiceName() != null) {
            existingService.setServiceName(
                    serviceDetails.getServiceName());
        }

        if (serviceDetails.getServiceDescription() != null) {
            existingService.setServiceDescription(
                    serviceDetails.getServiceDescription());
        }

        if (serviceDetails.getPrice() != null) {
            existingService.setPrice(
                    serviceDetails.getPrice());
        }

        if (serviceDetails.getDuration() != null) {
            existingService.setDuration(
                    serviceDetails.getDuration());
        }

        if (serviceDetails.getOrderType() != null) {
            existingService.setOrderType(
                    serviceDetails.getOrderType());
        }

        if (serviceDetails.getSuggestion() != null) {
            existingService.setSuggestion(
                    serviceDetails.getSuggestion());
        }

        if (serviceDetails.getVisibility() != null) {
            existingService.setVisibility(
                    serviceDetails.getVisibility());
        }

        if (serviceDetails.getBadges() != null) {
            existingService.setBadges(
                    serviceDetails.getBadges());
        }

        if (serviceDetails.getThumbnailUrl() != null
                && serviceDetails.getThumbnailPublicId() != null) {

            try {

                mediaValidationService.validateThumbnail(
                        serviceDetails.getThumbnailPublicId());

            } catch (Exception e) {

                throw new MediaValidationException(
                        "Thumbnail validation failed: "
                                + e.getMessage());
            }

            existingService.setThumbnailUrl(
                    serviceDetails.getThumbnailUrl());

            existingService.setThumbnailPublicId(
                    serviceDetails.getThumbnailPublicId());
        }

        if (serviceDetails.getPromoVideoUrl() != null
                && serviceDetails.getVideoPublicId() != null) {

            try {

                mediaValidationService.validateVideo(
                        serviceDetails.getVideoPublicId());

            } catch (Exception e) {

                throw new MediaValidationException(
                        "Video validation failed: "
                                + e.getMessage());
            }

            existingService.setPromoVideoUrl(
                    serviceDetails.getPromoVideoUrl());

            existingService.setVideoPublicId(
                    serviceDetails.getVideoPublicId());
        }

        return serviceRepository.save(existingService);
    }

    @Override
    public Optional<Service> getServiceById(
            String serviceId) {

        return serviceRepository.findByServiceId(serviceId);
    }

    @Override
    public List<Service> getServicesByName(
            String serviceName) {

        return serviceRepository.findByServiceName(serviceName);
    }

    @Override
    public List<Service> searchServices(
            String serviceName) {

        return serviceRepository
                .findByServiceNameContaining(serviceName);
    }

    @Override
    public List<Service> getServicesByBusinessId(
            String businessId) {

        return serviceRepository.findByBusinessId(businessId);
    }

    @Override
    public List<Service> getServicesByVisibility(
            String visibility) {

        return serviceRepository.findByVisibility(visibility);
    }

    @Override
    public List<Service> getServicesByBadges(
            String badges) {

        return serviceRepository.findByBadges(badges);
    }

    @Override
    public List<Service> getAllServices() {

        return serviceRepository.findAll();
    }

    @Override
    public void deleteService(
            String serviceId) {

        Service service =
                serviceRepository.findByServiceId(serviceId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Service not found with id: "
                                                + serviceId));

        businessRepository.findByBusinessId(
                        service.getBusinessId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Business not found for service: "
                                        + service.getServiceId()));

        serviceRepository.delete(service);
    }

    @Override
    public ServiceResponseDTO toResponseDTO(
            Service service) {

        if (service == null) {
            return null;
        }

        String businessName =
                getBusinessName(service.getBusinessId());

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
                service.getVideoPublicId()
        );
    }

    @Override
    public ServiceSummaryDTO toSummaryDTO(
            Service service) {

        if (service == null) {
            return null;
        }

        String businessName =
                getBusinessName(service.getBusinessId());

        return new ServiceSummaryDTO(
                service.getServiceId(),
                service.getServiceName(),
                service.getBusinessId(),
                businessName,
                service.getPrice(),
                service.getServiceDescription()
        );
    }

    private String getBusinessName(
            String businessId) {

        return businessRepository
                .findByBusinessId(businessId)
                .map(Business::getBusinessName)
                .orElse(null);
    }

	
}
