package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.locato.constants.events.user.UserCreatedEvent;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.CustomerProfileSummaryDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.CustomerProfile;

public interface CustomerProfileService {

    CustomerProfile createCustomerProfile(
            CustomerProfile customerProfile);

    void createCustomerProfileFromUser(
            UserCreatedEvent event);

    CustomerProfile updateCustomerProfile(
            String customerId,
            CustomerProfile customerProfileDetails);

    Optional<CustomerProfile> getCustomerProfileByUserId(
            String userId);

    Optional<CustomerProfile> getCustomerProfileByEmail(
            String email);

    List<CustomerProfile> getAllCustomerProfiles();

    void deleteCustomerProfile(
            String customerId);

    CustomerProfileResponseDTO toResponseDTO(
            CustomerProfile profile);

    CustomerProfileSummaryDTO toSummaryDTO(
            CustomerProfile profile);

    PageResponse<CustomerProfileResponseDTO> getAllcustomers(
            Pageable pageable);
}