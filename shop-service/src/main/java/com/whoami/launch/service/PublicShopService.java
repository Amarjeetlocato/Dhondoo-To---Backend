package com.whoami.launch.service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicShopResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;

public interface PublicShopService {

    PublicShopResponse getShopBySlug(String slug);

    PageResponse<ProductResponseDTO> getProducts(
            String slug,
            int page,
            int size);

    PageResponse<ServiceResponseDTO> getServices(
            String slug,
            int page,
            int size);

    PageResponse<ReelResponseDTO> getReels(
            String slug,
            int page,
            int size);

    PageResponse<ReviewResponse> getReviews(
            String slug,
            int page,
            int size);
}
