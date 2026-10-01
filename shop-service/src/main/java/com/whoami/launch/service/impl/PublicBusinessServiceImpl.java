package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicBusinessResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ReviewRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.PublicBusinessService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicBusinessServiceImpl
        implements PublicBusinessService {

    private final BusinessRepository businessRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final ReelRepository reelRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public PublicBusinessResponse getBusinessBySlug(String slug) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PageResponse<ProductResponseDTO> getProducts(
            String slug,
            int page,
            int size) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PageResponse<ServiceResponseDTO> getServices(
            String slug,
            int page,
            int size) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PageResponse<ReelResponseDTO> getReels(
            String slug,
            int page,
            int size) {

        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public PageResponse<ReviewResponse> getReviews(
            String slug,
            int page,
            int size) {

        throw new UnsupportedOperationException("Not implemented yet");
    }
}