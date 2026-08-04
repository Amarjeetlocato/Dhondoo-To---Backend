package com.whoami.launch.service;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicShopResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ReviewRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.repository.ShopRepository;
import com.whoami.launch.service.PublicShopService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PublicShopServiceImpl implements PublicShopService {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final ServiceRepository serviceRepository;
    private final ReelRepository reelRepository;
    private final ReviewRepository reviewRepository;

    @Override
    public PublicShopResponse getShopBySlug(String slug) {
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