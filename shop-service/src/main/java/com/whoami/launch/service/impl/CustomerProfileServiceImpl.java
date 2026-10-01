package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.events.user.UserCreatedEvent;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.CustomerProfileSummaryDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.producer.CustomerEventProducer;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.service.CustomerProfileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl
        implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    private final CustomerEventProducer customerEventProducer;

    @Override
    public CustomerProfile createCustomerProfile(
            CustomerProfile customerProfile) {

        if (customerProfileRepository.existsByUserId(
                customerProfile.getUserId())) {

            throw new RuntimeException(
                    "CustomerProfile already exists for userId: "
                            + customerProfile.getUserId()
            );
        }

        return customerProfileRepository.save(customerProfile);
    }

    @Override
    public void createCustomerProfileFromUser(
            UserCreatedEvent event) {

        if (customerProfileRepository.existsByUserId(
                event.getUserId())) {

            return;
        }

        CustomerProfile profile =
                new CustomerProfile();

        profile.setUserId(event.getUserId());
        profile.setFullName(event.getFullName());
        profile.setEmail(event.getEmail());

        CustomerProfile savedProfile =
                customerProfileRepository.save(profile);

        customerEventProducer.publishCustomerProfileCreated(
                CustomerProfileCreatedEvent.builder()
                        .customerId(savedProfile.getCustomerId())
                        .userId(savedProfile.getUserId())
                        .fullName(savedProfile.getFullName())
                        .email(savedProfile.getEmail())
                        .build()
        );
    }

    @Override
    public CustomerProfile updateCustomerProfile(
            String customerId,
            CustomerProfile customerProfileDetails) {

        /*
         * customerId is the public CUSTOMER_... ID.
         *
         * Current repository does not yet expose
         * findByCustomerId(), so this lookup will be
         * completed when that repository method is added.
         */
        throw new UnsupportedOperationException(
                "CustomerProfile lookup by customerId requires "
                        + "findByCustomerId(String) in CustomerProfileRepository"
        );
    }

    @Override
    public Optional<CustomerProfile> getCustomerProfileByUserId(
            String userId) {

        return customerProfileRepository.findByUserId(userId);
    }

    @Override
    public Optional<CustomerProfile> getCustomerProfileByEmail(
            String email) {

        return customerProfileRepository.findByEmail(email);
    }

    @Override
    public List<CustomerProfile> getAllCustomerProfiles() {

        return customerProfileRepository.findAll();
    }

    @Override
    public void deleteCustomerProfile(
            String customerId) {

        /*
         * Same public-ID issue as updateCustomerProfile().
         * Do not pass CUSTOMER_... to JpaRepository.findById()
         * because the entity's internal ID is Long.
         */
        throw new UnsupportedOperationException(
                "CustomerProfile deletion by customerId requires "
                        + "findByCustomerId(String) in CustomerProfileRepository"
        );
    }

    @Override
    public CustomerProfileResponseDTO toResponseDTO(
            CustomerProfile profile) {

        if (profile == null) {
            return null;
        }

        return new CustomerProfileResponseDTO(
                profile.getCustomerId(),
                profile.getUserId(),
                profile.getFullName(),
                profile.getEmail(),
                profile.getLogoUrl(),
                profile.getBannerUrl()
        );
    }

    @Override
    public CustomerProfileSummaryDTO toSummaryDTO(
            CustomerProfile profile) {

        if (profile == null) {
            return null;
        }

        return new CustomerProfileSummaryDTO(
                profile.getCustomerId(),
                profile.getUserId(),
                profile.getFullName(),
                profile.getEmail()
        );
    }

    @Override
    public PageResponse<CustomerProfileResponseDTO> getAllcustomers(
            Pageable pageable) {

        Page<CustomerProfile> page =
                customerProfileRepository.findAll(pageable);

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
}