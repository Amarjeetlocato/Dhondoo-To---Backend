package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.locato.constants.events.customer.CustomerProfileCreatedEvent;
import com.locato.constants.events.customer.CustomerProfileDeletedEvent;
import com.locato.constants.events.customer.CustomerProfileUpdatedEvent;
import com.locato.constants.events.user.UserCreatedEvent;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.CustomerProfileSummaryDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.producer.CustomerEventProducer;
import com.whoami.launch.repository.CustomerProfileRepository;
import com.whoami.launch.service.CustomerProfileService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomerProfileServiceImpl
        implements CustomerProfileService {

    private final CustomerProfileRepository customerProfileRepository;

    private final CustomerEventProducer customerEventProducer;

    @Override
    @Transactional
    public CustomerProfile createCustomerProfile(
            CustomerProfile customerProfile) {

        if (customerProfileRepository.existsByUserId(
                customerProfile.getUserId())) {

            throw new IllegalStateException(
                    "CustomerProfile already exists for userId: "
                            + customerProfile.getUserId());
        }

        if (customerProfileRepository.findByEmail(
                customerProfile.getEmail()).isPresent()) {

            throw new IllegalStateException(
                    "CustomerProfile already exists for email: "
                            + customerProfile.getEmail());
        }

        CustomerProfile savedProfile =
                customerProfileRepository.save(customerProfile);

        customerEventProducer.publishCustomerProfileCreated(
                CustomerProfileCreatedEvent.builder()
                        .customerId(savedProfile.getCustomerId())
                        .userId(savedProfile.getUserId())
                        .fullName(savedProfile.getFullName())
                        .email(savedProfile.getEmail())
                        .build());

        return savedProfile;
    }

    @Override
    @Transactional
    public void createCustomerProfileFromUser(
            UserCreatedEvent event) {

        if (customerProfileRepository.existsByUserId(
                event.getUserId())) {
            return;
        }

        // Avoid creating a second profile with an existing email.
        if (event.getEmail() != null
                && customerProfileRepository.findByEmail(
                        event.getEmail()).isPresent()) {

            return;
        }

        CustomerProfile profile = new CustomerProfile();

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
                        .build());
    }

    @Override
    @Transactional
    public CustomerProfile updateCustomerProfile(
            String customerId,
            CustomerProfile customerProfileDetails) {

        CustomerProfile existingProfile = customerProfileRepository
                .findByCustomerId(customerId)
                .or(() -> customerProfileRepository.findByUserId(customerId))
                .orElseGet(() -> {
                    CustomerProfile p = new CustomerProfile();
                    p.setUserId(customerId);
                    p.setFullName("User " + customerId.replace("USER_", ""));
                    p.setEmail(customerId.toLowerCase() + "@dhondoto.com");
                    return p;
                });

        boolean nameChanged = false;
        boolean emailChanged = false;

        if (customerProfileDetails.getFullName() != null
                && !customerProfileDetails.getFullName()
                        .equals(existingProfile.getFullName())) {

            existingProfile.setFullName(
                    customerProfileDetails.getFullName());

            nameChanged = true;
        }

        if (customerProfileDetails.getEmail() != null
                && !customerProfileDetails.getEmail()
                        .equals(existingProfile.getEmail())) {

            Optional<CustomerProfile> emailOwner =
                    customerProfileRepository.findByEmail(
                            customerProfileDetails.getEmail());

            if (emailOwner.isPresent()
                    && !emailOwner.get().getCustomerId()
                            .equals(customerId)) {

                throw new IllegalStateException(
                        "Email is already used by another customer");
            }

            existingProfile.setEmail(
                    customerProfileDetails.getEmail());

            emailChanged = true;
        }

        if (customerProfileDetails.getLogoUrl() != null) {
            existingProfile.setLogoUrl(
                    customerProfileDetails.getLogoUrl());
        }

        if (customerProfileDetails.getBannerUrl() != null) {
            existingProfile.setBannerUrl(
                    customerProfileDetails.getBannerUrl());
        }

        CustomerProfile savedProfile =
                customerProfileRepository.save(existingProfile);

        if (nameChanged || emailChanged) {
            String changes = nameChanged && emailChanged
                    ? "fullName,email"
                    : nameChanged
                            ? "fullName"
                            : "email";

            customerEventProducer.publishCustomerProfileUpdated(
                    CustomerProfileUpdatedEvent.builder()
                            .customerId(savedProfile.getCustomerId())
                            .userId(savedProfile.getUserId())
                            .fullName(savedProfile.getFullName())
                            .email(savedProfile.getEmail())
                            .changes(changes)
                            .build());
        } else if (customerProfileDetails.getLogoUrl() != null
                || customerProfileDetails.getBannerUrl() != null) {

            customerEventProducer.publishCustomerProfileUpdated(
                    CustomerProfileUpdatedEvent.builder()
                            .customerId(savedProfile.getCustomerId())
                            .userId(savedProfile.getUserId())
                            .fullName(savedProfile.getFullName())
                            .email(savedProfile.getEmail())
                            .changes("profileMedia")
                            .build());
        }

        return savedProfile;
    }

    @Override
    public Optional<CustomerProfile> getCustomerProfileByUserId(
            String userId) {

        Optional<CustomerProfile> profileOpt = customerProfileRepository.findByUserId(userId);
        if (profileOpt.isPresent()) {
            return profileOpt;
        }

        // Auto-create / Auto-heal customer profile if missing in DB
        if (userId != null && !userId.isBlank() && !userId.equalsIgnoreCase("null")) {
            CustomerProfile newProfile = new CustomerProfile();
            newProfile.setUserId(userId);
            newProfile.setFullName("User " + userId.replace("USER_", ""));
            newProfile.setEmail(userId.toLowerCase() + "@dhondoto.com");
            return Optional.of(customerProfileRepository.save(newProfile));
        }

        return Optional.empty();
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
    @Transactional
    public void deleteCustomerProfile(String customerId) {

        CustomerProfile profile =
                customerProfileRepository
                        .findByCustomerId(customerId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Customer profile not found: "
                                                + customerId));

        // Publish before deleting so event data remains available.
        customerEventProducer.publishCustomerProfileDeleted(
                CustomerProfileDeletedEvent.builder()
                        .customerId(profile.getCustomerId())
                        .userId(profile.getUserId())
                        .fullName(profile.getFullName())
                        .email(profile.getEmail())
                        .build());

        customerProfileRepository.delete(profile);
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
                profile.getBannerUrl());
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
                profile.getEmail());
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
                page.isLast());
    }
}