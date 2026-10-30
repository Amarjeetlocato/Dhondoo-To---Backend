package com.whoami.launch.service.impl;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.locato.enums.BusinessStatus;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicBusinessResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.Product;
import com.whoami.launch.entity.Reel;
import com.whoami.launch.entity.Service;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.repository.ReelRepository;
import com.whoami.launch.repository.ReviewRepository;
import com.whoami.launch.repository.ServiceRepository;
import com.whoami.launch.service.PublicBusinessService;

import lombok.RequiredArgsConstructor;

@org.springframework.stereotype.Service
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

        Business business =
                businessRepository.findBySlug(slug)
                        .or(() -> businessRepository.findByBusinessId(slug))
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Business not found for identifier: " + slug));

        boolean hideContent =
                shouldHideContentWhenClosed(business);

        long totalProducts = hideContent
                ? 0L
                : productRepository.countByBusinessId(
                        business.getBusinessId());

        long totalServices = hideContent
                ? 0L
                : serviceRepository.countByBusinessId(
                        business.getBusinessId());

        long totalReels = hideContent
                ? 0L
                : reelRepository.countByBusinessId(
                        business.getBusinessId());

        PublicBusinessResponse response =
                new PublicBusinessResponse();

        response.setBusinessId(business.getBusinessId());
        response.setSlug(business.getSlug());
        response.setBusinessName(business.getBusinessName());
        response.setMobileNumber(business.getMobileNumber());

        response.setAddress(business.getAddress());
        response.setVillage(business.getVillage());
        response.setBlock(business.getBlock());
        response.setDistrict(business.getDistrict());
        response.setState(business.getState());
        response.setCountry(business.getCountry());
        response.setPincode(business.getPincode());

        response.setLatitude(business.getLatitude());
        response.setLongitude(business.getLongitude());

        response.setBusinessStatus(
                business.getBusinessStatus());

        response.setAcceptingOrders(
                business.getAcceptingOrders());

        response.setAutoMode(
                business.getAutoMode());

        response.setOpeningTime(
                business.getOpeningTime());

        response.setClosingTime(
                business.getClosingTime());

        response.setFollowers(0L);

        response.setAverageRating(null);
        response.setTotalReviews(0L);

        response.setTotalProducts(totalProducts);
        response.setTotalServices(totalServices);
        response.setTotalReels(totalReels);

        return response;
    }

    @Override
    public PageResponse<ProductResponseDTO> getProducts(
            String slug,
            int page,
            int size) {

        Business business =
                getBusinessBySlugEntity(slug);

        if (shouldHideContentWhenClosed(business)) {
            return emptyPage(page, size);
        }

        Pageable pageable =
                createPageable(page, size);

        Page<Product> productPage =
                productRepository.findByBusinessId(
                        business.getBusinessId(),
                        pageable);

        List<ProductResponseDTO> content =
                productPage.getContent()
                        .stream()
                        .map(this::mapProduct)
                        .toList();

        return new PageResponse<>(
                content,
                productPage.getNumber(),
                productPage.getSize(),
                productPage.getTotalElements(),
                productPage.getTotalPages(),
                productPage.isFirst(),
                productPage.isLast()
        );
    }

    @Override
    public PageResponse<ServiceResponseDTO> getServices(
            String slug,
            int page,
            int size) {

        Business business =
                getBusinessBySlugEntity(slug);

        if (shouldHideContentWhenClosed(business)) {
            return emptyPage(page, size);
        }

        Pageable pageable =
                createPageable(page, size);

        Page<Service> servicePage =
                serviceRepository.findByBusinessId(
                        business.getBusinessId(),
                        pageable);

        List<ServiceResponseDTO> content =
                servicePage.getContent()
                        .stream()
                        .map(this::mapService)
                        .toList();

        return new PageResponse<>(
                content,
                servicePage.getNumber(),
                servicePage.getSize(),
                servicePage.getTotalElements(),
                servicePage.getTotalPages(),
                servicePage.isFirst(),
                servicePage.isLast()
        );
    }

    @Override
    public PageResponse<ReelResponseDTO> getReels(
            String slug,
            int page,
            int size) {

        Business business =
                getBusinessBySlugEntity(slug);

        Pageable pageable =
                createPageable(page, size);

        Page<Reel> reelPage =
                reelRepository.findByBusinessId(
                        business.getBusinessId(),
                        pageable);

        List<ReelResponseDTO> content =
                reelPage.getContent()
                        .stream()
                        .map(this::mapReel)
                        .toList();

        return new PageResponse<>(
                content,
                reelPage.getNumber(),
                reelPage.getSize(),
                reelPage.getTotalElements(),
                reelPage.getTotalPages(),
                reelPage.isFirst(),
                reelPage.isLast()
        );
    }

    @Override
    public PageResponse<ReviewResponse> getReviews(
            String slug,
            int page,
            int size) {

        Pageable pageable =
                createPageable(page, size);

        return new PageResponse<>(
                List.of(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0L,
                0,
                true,
                true
        );
    }

    private Business getBusinessBySlugEntity(
            String slug) {

        return businessRepository.findBySlug(slug)
                .or(() -> businessRepository.findByBusinessId(slug))
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Business not found for identifier: " + slug));
    }

    private boolean shouldHideContentWhenClosed(
            Business business) {

        return business.getBusinessStatus()
                        == BusinessStatus.CLOSED
                && Boolean.TRUE.equals(
                        business.getHideProductsWhenClosed());
    }

    private Pageable createPageable(
            int page,
            int size) {

        if (page < 0) {
            page = 0;
        }

        if (size <= 0) {
            size = 20;
        }

        if (size > 100) {
            size = 100;
        }

        return PageRequest.of(page, size);
    }

    private <T> PageResponse<T> emptyPage(
            int page,
            int size) {

        Pageable pageable =
                createPageable(page, size);

        return new PageResponse<>(
                List.of(),
                pageable.getPageNumber(),
                pageable.getPageSize(),
                0L,
                0,
                true,
                true
        );
    }

    private ProductResponseDTO mapProduct(
            Product product) {

        if (product == null) {
            return null;
        }

        return ProductResponseDTO.builder()
                .productId(product.getProductId())
                .businessId(product.getBusinessId())
                .productName(product.getProductName())
                .productPrice(product.getProductPrice())
                .productDescription(product.getProductDescription())
                .quantity(product.getQuantity())
                .stockStatus(product.getStockStatus())
                .productImages(product.getProductImages())
                .quality(product.getQuality())
                .orderType(product.getOrderType())
                .visibility(product.getVisibility())
                .badges(product.getBadges())
                .build();
    }

    private ServiceResponseDTO mapService(
            Service service) {

        if (service == null) {
            return null;
        }

        return new ServiceResponseDTO(
                service.getServiceId(),
                service.getBusinessId(),
                getBusinessName(
                        service.getBusinessId()),
                service.getServiceName(),
                service.getPrice(),
                service.getServiceDescription(),
                service.getDuration(),
                service.getOrderType(),
                service.getSuggestion(),
                service.getVisibility(),
                service.getBadges(),
                service.getThumbnailUrl(),
                service.getThumbnailPublicId(),
                service.getPromoVideoUrl(),
                service.getVideoPublicId()
        );
    }

    private ReelResponseDTO mapReel(
            Reel reel) {

        if (reel == null) {
            return null;
        }

        return new ReelResponseDTO(
                reel.getReelId(),
                reel.getBusinessId(),
                reel.getReelVideo(),
                reel.getReelThumbnail(),
                reel.getReelDescription(),
                reel.getReelReviews(),
                reel.getReelRatings()
        );
    }

    private String getBusinessName(
            String businessId) {

        return businessRepository
                .findByBusinessId(businessId)
                .map(Business::getBusinessName)
                .orElse(null);
    }
}
