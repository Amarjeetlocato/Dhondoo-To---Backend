package com.whoami.launch.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.locato.dto.ServiceCreatedEvent;
import com.locato.dto.ServiceDeletedEvent;
import com.locato.dto.ServiceUpdatedEvent;
import com.whoami.launch.config.MediaValidationService;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ServiceSummaryDTO;
import com.whoami.launch.entity.Service;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.exception.MediaValidationException;
import com.whoami.launch.producer.ShopKafkaProducer;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.repository.ShopFollowerRepository;
import com.whoami.launch.repository.ShopRepository;
import com.locato.topics.KafkaTopics;

@org.springframework.stereotype.Service
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private MediaValidationService mediaValidationService;

    @Autowired
    private ShopRepository shopRepository;

     
    @Autowired
    private ShopFollowerRepository followerRepository;
    
    @Autowired
    private ShopKafkaProducer shopKafkaProducer;
    
    private com.whoami.launch.util.notify notify;

    /* ===================== CREATE SERVICE ===================== */
    public Service createService(Service service) {

        Shop shop = shopRepository.findById(service.getShopId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Shop not found with id: "
                                        + service.getShopId()
                        ));

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
                    "Media validation failed: " + e.getMessage()
            );
        }

        Service saved = serviceRepository.save(service);

        ServiceCreatedEvent event =
                ServiceCreatedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("SERVICE_CREATED")
                        .eventTime(LocalDateTime.now())

                        .serviceId(saved.getServiceId())
                        .shopId(shop.getShopId())
                        .shopName(shop.getShopName())
                        .userId(shop.getUserId())

                        .serviceName(saved.getServiceName())
                        .price(saved.getPrice())
                        .serviceDescription(saved.getServiceDescription())
                        .serviceThumbnail(saved.getThumbnailUrl())

                        .build();
        shopKafkaProducer.publish(
                KafkaTopics.SERVICE_EVENTS,
                event
        );

        return saved;
    }
    
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
                page.isLast()
        );
    }

    /* ===================== UPDATE SERVICE ===================== */
    public Service updateService(String serviceId, Service serviceDetails) {

        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);

        if (serviceOpt.isEmpty()) {
            throw new RuntimeException("Service not found with id: " + serviceId);
        }

        Service existingService = serviceOpt.get();

        Shop shop = shopRepository.findById(existingService.getShopId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Shop not found for service: "
                                        + existingService.getServiceId()
                        ));
        StringBuilder changes = new StringBuilder();

        if (serviceDetails.getServiceName() != null) {
            existingService.setServiceName(serviceDetails.getServiceName());
            changes.append("Service name updated. ");
        }

        if (serviceDetails.getServiceDescription() != null) {
            existingService.setServiceDescription(serviceDetails.getServiceDescription());
            changes.append("Description updated. ");
        }

        if (serviceDetails.getPrice() != null) {
            existingService.setPrice(serviceDetails.getPrice());
            changes.append("Price updated. ");
        }

        if (serviceDetails.getDuration() != null) {
            existingService.setDuration(serviceDetails.getDuration());
            changes.append("Duration updated. ");
        }

        if (serviceDetails.getOrderType() != null) {
            existingService.setOrderType(serviceDetails.getOrderType());
            changes.append("Order type updated. ");
        }

        if (serviceDetails.getSuggestion() != null) {
            existingService.setSuggestion(serviceDetails.getSuggestion());
            changes.append("Suggestion updated. ");
        }

        if (serviceDetails.getVisibility() != null) {
            existingService.setVisibility(serviceDetails.getVisibility());
            changes.append("Visibility updated. ");
        }

        if (serviceDetails.getBadges() != null) {
            existingService.setBadges(serviceDetails.getBadges());
            changes.append("Badges updated. ");
        }

        /* Thumbnail update */
        if (serviceDetails.getThumbnailUrl() != null
                && serviceDetails.getThumbnailPublicId() != null) {

            try {
                mediaValidationService.validateThumbnail(
                        serviceDetails.getThumbnailPublicId()
                );
            } catch (Exception e) {
                throw new MediaValidationException(
                        "Thumbnail validation failed: " + e.getMessage()
                );
            }

            existingService.setThumbnailUrl(serviceDetails.getThumbnailUrl());
            existingService.setThumbnailPublicId(serviceDetails.getThumbnailPublicId());
            changes.append("Thumbnail updated. ");
        }

        /* Video update */
        if (serviceDetails.getPromoVideoUrl() != null
                && serviceDetails.getVideoPublicId() != null) {

            try {
                mediaValidationService.validateVideo(
                        serviceDetails.getVideoPublicId()
                );
            } catch (Exception e) {
                throw new MediaValidationException(
                        "Video validation failed: " + e.getMessage()
                );
            }

            existingService.setPromoVideoUrl(serviceDetails.getPromoVideoUrl());
            existingService.setVideoPublicId(serviceDetails.getVideoPublicId());
            changes.append("Video updated. ");
        }

        Service updated = serviceRepository.save(existingService);

       

        ServiceUpdatedEvent event =
                ServiceUpdatedEvent.builder()
                        .eventId(UUID.randomUUID().toString())
                        .eventType("SERVICE_UPDATED")
                        .eventTime(LocalDateTime.now())

                        .serviceId(updated.getServiceId())
                        .shopId(shop.getShopId())
                        .shopName(shop.getShopName())
                        .userId(shop.getUserId())

                        .serviceName(updated.getServiceName())
                        .price(updated.getPrice())
                        .serviceDescription(updated.getServiceDescription())
                        .serviceThumbnail(updated.getThumbnailUrl())
                        .changes(changes.toString())
                        .build();
        	shopKafkaProducer.publish(
        	        KafkaTopics.SERVICE_EVENTS,
        	        event
        	);
        

        return updated;
    }

    /* ===================== GET METHODS ===================== */
    public Optional<Service> getServiceById(String serviceId) {
        return serviceRepository.findById(serviceId);
    }

    public List<Service> getServicesByName(String serviceName) {
        return serviceRepository.findByServiceName(serviceName);
    }

    public List<Service> searchServices(String serviceName) {
        return serviceRepository.findByServiceNameContaining(serviceName);
    }

    public List<Service> getServicesByShopId(String shopId) {
        return serviceRepository.findByShopId(shopId);
    }

    public List<Service> getServicesByVisibility(String visibility) {
        return serviceRepository.findByVisibility(visibility);
    }

    public List<Service> getServicesByBadges(String badges) {
        return serviceRepository.findByBadges(badges);
    }

    public List<Service> getAllServices() {
        return serviceRepository.findAll();
    }

    /* ===================== DELETE SERVICE ===================== */
    public void deleteService(String serviceId) {

        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);

        if (serviceOpt.isPresent()) {

            Service service = serviceOpt.get();

            Shop shop = shopRepository.findById(service.getShopId())
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "Shop not found for service: "
                                            + service.getServiceId()
                            ));
            serviceRepository.deleteById(serviceId);

          
            ServiceDeletedEvent event =
                    ServiceDeletedEvent.builder()
                            .eventId(UUID.randomUUID().toString())
                            .eventType("SERVICE_DELETED")
                            .eventTime(LocalDateTime.now())

                            .serviceId(service.getServiceId())
                            .shopId(shop.getShopId())
                            .shopName(shop.getShopName())
                            .userId(shop.getUserId())

                            .serviceName(service.getServiceName())
                            .build();
            	shopKafkaProducer.publish(
            	        KafkaTopics.SERVICE_EVENTS,
            	        event
            	);
            
        }
    }

    /* ===================== DTO METHODS ===================== */
    public ServiceResponseDTO toResponseDTO(Service service) {

        if (service == null) return null;

        return new ServiceResponseDTO(
                service.getServiceId(),
                service.getShopId(),
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

    public ServiceSummaryDTO toSummaryDTO(Service service) {

        if (service == null) return null;

        return new ServiceSummaryDTO(
                service.getServiceId(),
                service.getServiceName(),
                service.getShopId(),
                service.getPrice(),
                service.getServiceDescription()
        );
    }

  
    
}