package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.CustomerEventProducer;
import com.whoami.launch.dto.CustomerProfileCreatedEvent;
import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.CustomerProfileSummaryDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.UserCreatedEvent;
import com.whoami.launch.entity.CustomerProfile;
import com.whoami.launch.repository.CustomerProfileRepository;

@Service
public class CustomerProfileService {
    
    private final CustomerEventProducer customerEventProducer;


	@Autowired
    private CustomerProfileRepository customerProfileRepository;
    
   
   

	CustomerProfileService(CustomerEventProducer customerEventProducer) {
		this.customerEventProducer = customerEventProducer;
	}
    
    
    public CustomerProfile createCustomerProfile(CustomerProfile customerProfile) {
        if (customerProfileRepository.existsByUserId(customerProfile.getUserId())) {
            throw new RuntimeException("CustomerProfile already exists for userId: " + customerProfile.getUserId());
        }
        return customerProfileRepository.save(customerProfile);
    }
    
    public void createCustomerProfileFromUser(UserCreatedEvent event) {

        if (customerProfileRepository.existsByUserId(event.getUserId())) {
            return;
        }

        CustomerProfile profile = new CustomerProfile();

        profile.setUserId(event.getUserId());
        profile.setUsername(event.getUsername());
        profile.setEmail(event.getEmail());

        CustomerProfile savedProfile = customerProfileRepository.save(profile);

        customerEventProducer.publishCustomerProfileCreated(
                CustomerProfileCreatedEvent.builder()
                        .customerId(savedProfile.getCustomerId())
                        .userId(savedProfile.getUserId())
                        .username(savedProfile.getUsername())
                        .email(savedProfile.getEmail())
                        .build()
        );
    }
    
    public CustomerProfile updateCustomerProfile(String customerId, CustomerProfile customerProfileDetails) {
        Optional<CustomerProfile> profile = customerProfileRepository.findById(customerId);
        if (profile.isPresent()) {
            CustomerProfile existingProfile = profile.get();
            if (customerProfileDetails.getUsername() != null) {
                existingProfile.setUsername(customerProfileDetails.getUsername());
            }
            if (customerProfileDetails.getEmail() != null) {
                existingProfile.setEmail(customerProfileDetails.getEmail());
            }
            if (customerProfileDetails.getLogoUrl() != null) {
                existingProfile.setLogoUrl(customerProfileDetails.getLogoUrl());
            }
            if (customerProfileDetails.getBannerUrl() != null) {
                existingProfile.setBannerUrl(customerProfileDetails.getBannerUrl());
            }
            return customerProfileRepository.save(existingProfile);
        }
        return null;
    }
    
    public Optional<CustomerProfile> getCustomerProfileById(String customerId) {
        return customerProfileRepository.findById(customerId);
    }
    
    public Optional<CustomerProfile> getCustomerProfileByUserId(String userId) {
        return customerProfileRepository.findByUserId(userId);
    }
    
    public Optional<CustomerProfile> getCustomerProfileByUsername(String username) {
        return customerProfileRepository.findByUsername(username);
    }
    
    public Optional<CustomerProfile> getCustomerProfileByEmail(String email) {
        return customerProfileRepository.findByEmail(email);
    }
    
    public List<CustomerProfile> getAllCustomerProfiles() {
        return customerProfileRepository.findAll();
    }
    
    public void deleteCustomerProfile(String customerId) {
        customerProfileRepository.deleteById(customerId);
    }
    
    public CustomerProfileResponseDTO toResponseDTO(CustomerProfile profile) {
        if (profile == null) return null;
        return new CustomerProfileResponseDTO(
            profile.getCustomerId(),
            profile.getUserId(),
            profile.getUsername(),
            profile.getEmail(),
            profile.getLogoUrl(),
            profile.getBannerUrl()
        );
    }
    
    public CustomerProfileSummaryDTO toSummaryDTO(CustomerProfile profile) {
        if (profile == null) return null;
        return new CustomerProfileSummaryDTO(
            profile.getCustomerId(),
            profile.getUserId(),
            profile.getUsername(),
            profile.getEmail()
        );
    }
    public PageResponse<CustomerProfileResponseDTO> getAllcustomers(
            Pageable pageable) {

        Page<CustomerProfile> page = customerProfileRepository.findAll(pageable);

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
