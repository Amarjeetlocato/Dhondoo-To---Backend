 package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.enums.BusinessStatus;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessSummaryDTO;
import com.whoami.launch.dto.FollowBusinessResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.entity.BusinessFollower;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.producer.ShopKafkaProducer;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.repository.BusinessFollowerRepository;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.BusinessService;
import com.whoami.launch.util.SlugUtil;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BusinessServiceImpl implements BusinessService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ReelRepository reelRepository;
    private final ServiceRepository serviceRepository;
    private final CustomerProfileRepository customerProfileRepository;
    private final BusinessFollowerRepository followerRepository;
    private final ShopKafkaProducer shopKafkaProducer;
    private final BusinessAccessStateRepository accessStateRepository;

    @Override
    @Transactional
    public Business createBusiness(Business business) {
        if (business == null) {
            business = new Business();
        }

        if (business.getBusinessName() == null || business.getBusinessName().isBlank()) {
            business.setBusinessName("Dhondoo Shop " + (System.currentTimeMillis() % 10000));
        }

        if (business.getUserId() == null || business.getUserId().isBlank()) {
            business.setUserId("USER_" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase());
        }

        if (business.getBusinessType() == null) {
            business.setBusinessType(com.locato.enums.BusinessType.RETAIL);
        }

        if (business.getMobileNumber() == null || business.getMobileNumber().isBlank()) {
            business.setMobileNumber("91" + (System.currentTimeMillis() % 1000000000L));
        }

        if (business.getAddress() == null || business.getAddress().isBlank()) {
            business.setAddress("India");
        }

        if (business.getLatitude() == null) {
            business.setLatitude(28.6139);
        }

        if (business.getLongitude() == null) {
            business.setLongitude(77.2090);
        }

        if (business.getBusinessStatus() == null) {
            business.setBusinessStatus(BusinessStatus.OPEN);
        }

        if (business.getAcceptingOrders() == null) {
            business.setAcceptingOrders(true);
        }

        if (business.getHideProductsWhenClosed() == null) {
            business.setHideProductsWhenClosed(false);
        }

        if (business.getAutoMode() == null) {
            business.setAutoMode(false);
        }

        if (business.getBusinessId() == null) {
            business.setBusinessId("BUSINESS_" + UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase());
        }

        Optional<Business> existingByMobile = businessRepository.findByMobileNumber(business.getMobileNumber());
        if (existingByMobile.isPresent()) {
            business.setMobileNumber(business.getMobileNumber() + "_" + (System.currentTimeMillis() % 1000));
        }

        String slug = business.getSlug();
        if (slug == null || slug.isBlank()) {
            do {
                slug = SlugUtil.generate(business.getBusinessName());
            } while (businessRepository.existsBySlug(slug));
            business.setSlug(slug);
        }

        Business savedBusiness = businessRepository.save(business);

        BusinessAccessState accessState = accessStateRepository.findByBusinessId(savedBusiness.getBusinessId())
                .orElseGet(() -> {
                    BusinessAccessState state = new BusinessAccessState();
                    state.setBusinessId(savedBusiness.getBusinessId());
                    state.setVerified(false);
                    state.setSubscriptionActive(false);
                    return state;
                });
        accessStateRepository.save(accessState);

        try {
            shopKafkaProducer.publishBusinessCreated(savedBusiness);
        } catch (Exception e) {
            // Log kafka exception
        }

        try {
            syncBusinessChatProfile(savedBusiness);
        } catch (Exception e) {
            log.debug("Could not sync business chat profile: {}", e.getMessage());
        }

        return savedBusiness;
    }

    @Override
    @Transactional
    public Business updateBusiness(
            String businessId,
            Business businessDetails) {

        Business existingBusiness =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: " + businessId));

        String previousStatus =
                existingBusiness.getBusinessStatus() == null
                        ? null
                        : existingBusiness.getBusinessStatus().name();

        boolean isAttemptingCoreDetailsChange =
                (businessDetails.getBusinessName() != null && !businessDetails.getBusinessName().equals(existingBusiness.getBusinessName())) ||
                (businessDetails.getMobileNumber() != null && !businessDetails.getMobileNumber().equals(existingBusiness.getMobileNumber())) ||
                (businessDetails.getAddress() != null && !businessDetails.getAddress().equals(existingBusiness.getAddress())) ||
                (businessDetails.getVillage() != null && !businessDetails.getVillage().equals(existingBusiness.getVillage())) ||
                (businessDetails.getBlock() != null && !businessDetails.getBlock().equals(existingBusiness.getBlock())) ||
                (businessDetails.getDistrict() != null && !businessDetails.getDistrict().equals(existingBusiness.getDistrict())) ||
                (businessDetails.getState() != null && !businessDetails.getState().equals(existingBusiness.getState())) ||
                (businessDetails.getCountry() != null && !businessDetails.getCountry().equals(existingBusiness.getCountry())) ||
                (businessDetails.getPincode() != null && !businessDetails.getPincode().equals(existingBusiness.getPincode())) ||
                (businessDetails.getLatitude() != null && !businessDetails.getLatitude().equals(existingBusiness.getLatitude())) ||
                (businessDetails.getLongitude() != null && !businessDetails.getLongitude().equals(existingBusiness.getLongitude()));

        if (isAttemptingCoreDetailsChange) {
            throw new IllegalStateException(
                    "Direct change of business name, phone number, address, or location is not allowed. A change request must be submitted for review and approval."
            );
        }

        if (businessDetails.getBusinessStatus() != null) {
            existingBusiness.setBusinessStatus(
                    businessDetails.getBusinessStatus());
        }

        if (businessDetails.getAcceptingOrders() != null) {
            existingBusiness.setAcceptingOrders(
                    businessDetails.getAcceptingOrders());
        }

        if (businessDetails.getHideProductsWhenClosed() != null) {
            existingBusiness.setHideProductsWhenClosed(
                    businessDetails.getHideProductsWhenClosed());
        }

        if (businessDetails.getAutoMode() != null) {
            existingBusiness.setAutoMode(
                    businessDetails.getAutoMode());
        }

        if (businessDetails.getOpeningTime() != null) {
            existingBusiness.setOpeningTime(
                    businessDetails.getOpeningTime());
        }

        if (businessDetails.getClosingTime() != null) {
            existingBusiness.setClosingTime(
                    businessDetails.getClosingTime());
        }

        if (businessDetails.getUpiId() != null) {
            existingBusiness.setUpiId(
                    businessDetails.getUpiId());
        }

        if (businessDetails.getUpiId2() != null) {
            existingBusiness.setUpiId2(
                    businessDetails.getUpiId2());
        }

        if (businessDetails.getPaymentQrCode1Url() != null) {
            existingBusiness.setPaymentQrCode1Url(
                    businessDetails.getPaymentQrCode1Url());
        }

        if (businessDetails.getPaymentQrCode2Url() != null) {
            existingBusiness.setPaymentQrCode2Url(
                    businessDetails.getPaymentQrCode2Url());
        }

        if (businessDetails.getPaymentNotes() != null) {
            existingBusiness.setPaymentNotes(
                    businessDetails.getPaymentNotes());
        }

        if (businessDetails.getSocialLinks() != null) {
            existingBusiness.setSocialLinks(
                    businessDetails.getSocialLinks());
        }

        Business savedBusiness =
                businessRepository.save(existingBusiness);

        // Use a separate event if the status actually changed.
        String currentStatus =
                savedBusiness.getBusinessStatus() == null
                        ? null
                        : savedBusiness.getBusinessStatus().name();

        if (previousStatus != null
                && !previousStatus.equals(currentStatus)) {

            shopKafkaProducer.publishBusinessStatusChanged(
                    savedBusiness,
                    previousStatus);
        } else {
            shopKafkaProducer.publishBusinessUpdated(
                    savedBusiness,
                    "Business details updated");
        }

        try {
            syncBusinessChatProfile(savedBusiness);
        } catch (Exception e) {
            log.debug("Could not sync business chat profile: {}", e.getMessage());
        }

        return savedBusiness;
    }

    private void syncBusinessChatProfile(Business business) {
        String[] chatHosts = {
            "http://chat-service-chat-service.platform.svc.cluster.local:8085",
            "http://chat-service:8085",
            "http://localhost:8085"
        };
        java.util.Map<String, String> payload = new java.util.HashMap<>();
        payload.put("businessId", business.getBusinessId());
        payload.put("businessName", business.getBusinessName());
        payload.put("imageUrl", business.getLogoUrl());

        for (String host : chatHosts) {
            try {
                restTemplate.postForEntity(host + "/api/chat/businesses", payload, java.util.Map.class);
                log.info("Synced business chat profile to Servicechat for businessId={}", business.getBusinessId());
                break;
            } catch (Exception e) {
                log.debug("Chat service sync skipped at {}: {}", host, e.getMessage());
            }
        }
    }

    @Override
    public Optional<Business> getBusinessById(String businessId) {
        return businessRepository.findByBusinessId(businessId);
    }

    @Override
    public Optional<Business> getBusinessByName(String businessName) {
        return businessRepository.findByBusinessName(businessName);
    }

    @Override
    public List<Business> getBusinessesByUserId(String userId) {
        return businessRepository.findByUserId(userId);
    }

    @Override
    public List<Business> searchBusinesses(String businessName) {
        return businessRepository.findByBusinessNameContaining(businessName);
    }

    @Override
    public List<Business> getAllBusinesses() {
        return businessRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteBusiness(String businessId) {

        Business business =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: " + businessId));

        // Publish while the business data is still available.
        shopKafkaProducer.publishBusinessDeleted(business);

        businessRepository.delete(business);
    }

    @Override
    public BusinessResponseDTO toResponseDTO(Business business) {

        if (business == null) {
            return null;
        }

        long totalProducts =
                productRepository.countByBusinessId(
                        business.getBusinessId());

        long totalReels =
                reelRepository.countByBusinessId(
                        business.getBusinessId());

        long totalServices =
                serviceRepository.countByBusinessId(
                        business.getBusinessId());

        BusinessAccessState accessState = accessStateRepository
                .findByBusinessId(business.getBusinessId())
                .orElse(null);

        Boolean isVerified = accessState != null && accessState.isVerified();
        Boolean isSubscriptionActive = accessState != null && accessState.isSubscriptionActive();
        Boolean isPaymentCompleted = accessState != null && accessState.isPaymentCompleted();

        return new BusinessResponseDTO(
                business.getBusinessId(),
                business.getUserId(),
                business.getBusinessName(),
                business.getMobileNumber(),
                business.getAddress(),
                business.getVillage(),
                business.getBlock(),
                business.getDistrict(),
                business.getState(),
                business.getCountry(),
                business.getPincode(),
                business.getLatitude(),
                business.getLongitude(),
                totalProducts,
                totalReels,
                totalServices,
                business.getBusinessStatus(),
                business.getAcceptingOrders(),
                business.getHideProductsWhenClosed(),
                business.getAutoMode(),
                business.getOpeningTime(),
                business.getClosingTime(),
                isVerified,
                isSubscriptionActive,
                isPaymentCompleted,
                business.getUpiId(),
                business.getUpiId2(),
                business.getPaymentQrCode1Url(),
                business.getPaymentQrCode2Url(),
                business.getPaymentNotes(),
                business.getSocialLinks());
    }

    @Override
    public BusinessSummaryDTO toSummaryDTO(Business business) {

        if (business == null) {
            return null;
        }

        return new BusinessSummaryDTO(
                business.getBusinessId(),
                business.getBusinessName(),
                business.getUserId(),
                business.getVillage(),
                business.getDistrict(),
                business.getPincode(),
                business.getLatitude(),
                business.getLongitude(),
                business.getAddress(),
                business.getMobileNumber());
    }

    @Override
    public PageResponse<BusinessResponseDTO> getAllBusinesses(
            Pageable pageable) {

        Page<Business> page = businessRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream()
                        .map(this::toResponseDTO)
                        .toList(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast());
    }

    @Override
    @Transactional
    public Business updateBusinessStatus(
            String businessId,
            BusinessStatus status) {

        Business business =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: " + businessId));

        String previousStatus =
                business.getBusinessStatus() == null
                        ? null
                        : business.getBusinessStatus().name();

        // Avoid generating duplicate events for unchanged status.
        if (business.getBusinessStatus() == status) {
            return business;
        }

        business.setBusinessStatus(status);

        Business savedBusiness = businessRepository.save(business);

        shopKafkaProducer.publishBusinessStatusChanged(
                savedBusiness,
                previousStatus);

        return savedBusiness;
    }

    @Override
    public FollowBusinessResponse followBusiness(
            String businessId,
            String userId) {

        businessRepository
                .findByBusinessId(businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business not found: " + businessId));

        if (followerRepository
                .findByBusinessIdAndUserId(businessId, userId)
                .isPresent()) {

            return getFollowStatus(businessId, userId);
        }

        BusinessFollower follower = new BusinessFollower();
        follower.setBusinessId(businessId);
        follower.setUserId(userId);

        followerRepository.save(follower);

        return getFollowStatus(businessId, userId);
    }

    @Override
    public FollowBusinessResponse unfollowBusiness(
            String businessId,
            String userId) {

        followerRepository.deleteByBusinessIdAndUserId(businessId, userId);

        return getFollowStatus(businessId, userId);
    }

    @Override
    public FollowBusinessResponse getFollowStatus(
            String businessId,
            String userId) {

        boolean following =
                followerRepository
                        .findByBusinessIdAndUserId(businessId, userId)
                        .isPresent();

        long followers =
                followerRepository.countByBusinessId(businessId);

        return FollowBusinessResponse.builder()
                .businessId(businessId)
                .following(following)
                .followersCount(followers)
                .build();
    }

    @Override
    public void requestVerification(String businessId) {

        Business business =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: " + businessId));

        shopKafkaProducer.publishApplicationCreationRequested(business);
    }

    @Override
    @Transactional
    public void requestBusinessDetailsChange(String businessId, Business changeDetails) {

        Business existingBusiness = businessRepository
                .findByBusinessId(businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business not found: " + businessId));

        if (changeDetails.getBusinessName() != null) {
            existingBusiness.setBusinessName(changeDetails.getBusinessName());
        }
        if (changeDetails.getMobileNumber() != null) {
            existingBusiness.setMobileNumber(changeDetails.getMobileNumber());
        }
        if (changeDetails.getAddress() != null) {
            existingBusiness.setAddress(changeDetails.getAddress());
        }
        if (changeDetails.getLatitude() != null) {
            existingBusiness.setLatitude(changeDetails.getLatitude());
        }
        if (changeDetails.getLongitude() != null) {
            existingBusiness.setLongitude(changeDetails.getLongitude());
        }

        shopKafkaProducer.publishApplicationCreationRequested(existingBusiness);
    }
}