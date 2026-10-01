package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.locato.enums.BusinessStatus;
import com.whoami.launch.dto.BusinessResponseDTO;
import com.whoami.launch.dto.BusinessSummaryDTO;
import com.whoami.launch.dto.FollowBusinessResponse;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.Business;

public interface BusinessService {

    Business createBusiness(Business business);

    Business updateBusiness(
            String businessId,
            Business businessDetails);

    Optional<Business> getBusinessById(
            String businessId);

    Optional<Business> getBusinessByName(
            String businessName);

    List<Business> getBusinessesByUserId(
            String userId);

    List<Business> searchBusinesses(
            String businessName);

    List<Business> getAllBusinesses();

    void deleteBusiness(
            String businessId);

    BusinessResponseDTO toResponseDTO(
            Business business);

    BusinessSummaryDTO toSummaryDTO(
            Business business);

    PageResponse<BusinessResponseDTO> getAllBusinesses(
            Pageable pageable);

    Business updateBusinessStatus(
            String businessId,
            BusinessStatus status);

    FollowBusinessResponse followBusiness(
            String businessId,
            String userId);

    FollowBusinessResponse unfollowBusiness(
            String businessId,
            String userId);

    FollowBusinessResponse getFollowStatus(
            String businessId,
            String userId);
}