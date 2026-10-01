package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.enums.BusinessStatus;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessSummaryDTO;
import com.whoami.launch.dto.FollowBusinessResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.BusinessFollower;
import com.whoami.launch.exception.ResourceNotFoundException;
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

    @Override
    public Business createBusiness(
            Business business) {

        if (businessRepository
                .findByMobileNumber(
                        business.getMobileNumber())
                .isPresent()) {

            throw new RuntimeException(
                    "Business with mobile number "
                            + business.getMobileNumber()
                            + " already exists");
        }

        String slug;

        do {
            slug = SlugUtil.generate(
                    business.getBusinessName());

        } while (businessRepository.existsBySlug(slug));

        business.setSlug(slug);

        return businessRepository.save(business);
    }

    @Override
    public Business updateBusiness(
            String businessId,
            Business businessDetails) {

        Business existingBusiness =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: "
                                                + businessId));

        if (businessDetails.getBusinessName() != null) {
            existingBusiness.setBusinessName(
                    businessDetails.getBusinessName());
        }

        if (businessDetails.getAddress() != null) {
            existingBusiness.setAddress(
                    businessDetails.getAddress());
        }

        if (businessDetails.getMobileNumber() != null) {
            existingBusiness.setMobileNumber(
                    businessDetails.getMobileNumber());
        }

        if (businessDetails.getVillage() != null) {
            existingBusiness.setVillage(
                    businessDetails.getVillage());
        }

        if (businessDetails.getBlock() != null) {
            existingBusiness.setBlock(
                    businessDetails.getBlock());
        }

        if (businessDetails.getDistrict() != null) {
            existingBusiness.setDistrict(
                    businessDetails.getDistrict());
        }

        if (businessDetails.getState() != null) {
            existingBusiness.setState(
                    businessDetails.getState());
        }

        if (businessDetails.getCountry() != null) {
            existingBusiness.setCountry(
                    businessDetails.getCountry());
        }

        if (businessDetails.getPincode() != null) {
            existingBusiness.setPincode(
                    businessDetails.getPincode());
        }

        if (businessDetails.getLatitude() != null) {
            existingBusiness.setLatitude(
                    businessDetails.getLatitude());
        }

        if (businessDetails.getLongitude() != null) {
            existingBusiness.setLongitude(
                    businessDetails.getLongitude());
        }

        if (businessDetails.getBusinessStatus() != null) {
            existingBusiness.setBusinessStatus(
                    businessDetails.getBusinessStatus());
        }

        if (businessDetails.getAcceptingOrders() != null) {
            existingBusiness.setAcceptingOrders(
                    businessDetails.getAcceptingOrders());
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

        return businessRepository.save(
                existingBusiness);
    }

    @Override
    public Optional<Business> getBusinessById(
            String businessId) {

        return businessRepository.findByBusinessId(
                businessId);
    }

    @Override
    public Optional<Business> getBusinessByName(
            String businessName) {

        return businessRepository.findByBusinessName(
                businessName);
    }

    @Override
    public List<Business> getBusinessesByUserId(
            String userId) {

        return businessRepository.findByUserId(
                userId);
    }

    @Override
    public List<Business> searchBusinesses(
            String businessName) {

        return businessRepository
                .findByBusinessNameContaining(
                        businessName);
    }

    @Override
    public List<Business> getAllBusinesses() {

        return businessRepository.findAll();
    }

    @Override
    public void deleteBusiness(
            String businessId) {

        Business business =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: "
                                                + businessId));

        businessRepository.delete(business);
    }

    @Override
    public BusinessResponseDTO toResponseDTO(
            Business business) {

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
                business.getAutoMode(),

                business.getOpeningTime(),
                business.getClosingTime()
        );
    }

    @Override
    public BusinessSummaryDTO toSummaryDTO(
            Business business) {

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
                business.getMobileNumber()
        );
    }

    @Override
    public PageResponse<BusinessResponseDTO> getAllBusinesses(
            Pageable pageable) {

        Page<Business> page =
                businessRepository.findAll(pageable);

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
    @Transactional
    public Business updateBusinessStatus(
            String businessId,
            BusinessStatus status) {

        Business business =
                businessRepository
                        .findByBusinessId(businessId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Business not found: "
                                                + businessId));

        business.setBusinessStatus(status);

        return businessRepository.save(
                business);
    }

    @Override
    public FollowBusinessResponse followBusiness(
            String businessId,
            String userId) {

        businessRepository
                .findByBusinessId(businessId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Business not found: "
                                        + businessId));

        if (followerRepository
                .findByBusinessIdAndUserId(
                        businessId,
                        userId)
                .isPresent()) {

            return getFollowStatus(
                    businessId,
                    userId);
        }

        BusinessFollower follower =
                new BusinessFollower();

        follower.setBusinessId(businessId);
        follower.setUserId(userId);

        followerRepository.save(follower);

        return getFollowStatus(
                businessId,
                userId);
    }

    @Override
    public FollowBusinessResponse unfollowBusiness(
            String businessId,
            String userId) {

        followerRepository
                .deleteByBusinessIdAndUserId(
                        businessId,
                        userId);

        return getFollowStatus(
                businessId,
                userId);
    }

    @Override
    public FollowBusinessResponse getFollowStatus(
            String businessId,
            String userId) {

        boolean following =
                followerRepository
                        .findByBusinessIdAndUserId(
                                businessId,
                                userId)
                        .isPresent();

        long followers =
                followerRepository.countByBusinessId(
                        businessId);

        return FollowBusinessResponse.builder()
                .businessId(businessId)
                .following(following)
                .followersCount(followers)
                .build();
    }

	
}