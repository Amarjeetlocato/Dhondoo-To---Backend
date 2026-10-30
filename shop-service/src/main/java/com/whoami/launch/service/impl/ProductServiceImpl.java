
package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.locato.constants.events.product.ProductCreatedEvent;
import com.locato.constants.events.product.ProductDeletedEvent;
import com.locato.constants.events.product.ProductUpdatedEvent;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.entity.Business;
import com.whoami.launch.entity.Product;
import com.whoami.launch.entity.BusinessAccessState;
import com.whoami.launch.enums.BusinessCapability;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;
import com.whoami.launch.producer.ProductEventProducer;
import com.whoami.launch.repository.BusinessAccessStateRepository;
import com.whoami.launch.repository.BusinessRepository;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.service.BusinessAccessService;
import com.whoami.launch.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final BusinessAccessService businessAccessService;
    private final BusinessAccessStateRepository accessStateRepository;
    private final ProductEventProducer productEventProducer;

    @Override
    @Transactional
    public Product createProduct(Product product) {
        if (product == null) {
            product = new Product();
        }

        if (product.getProductName() == null || product.getProductName().isBlank()) {
            product.setProductName("New Product " + (System.currentTimeMillis() % 1000));
        }

        if (product.getProductPrice() == null) {
            product.setProductPrice(0.0);
        }

        if (product.getStockStatus() == null) {
            product.setStockStatus(StockStatus.AVAILABLE);
        }

        if (product.getVisibility() == null) {
            product.setVisibility(ProductVisibility.PUBLIC);
        }

        if (product.getBusinessId() != null && !product.getBusinessId().isBlank()) {
            long currentProductCount = productRepository.countByBusinessId(product.getBusinessId());
            businessAccessService.validateCreationAccess(
                    product.getBusinessId(),
                    BusinessCapability.PRODUCT,
                    currentProductCount
            );
        }

        Product savedProduct = productRepository.save(product);

        try {
            if (savedProduct.getBusinessId() != null) {
                businessRepository.findByBusinessId(savedProduct.getBusinessId())
                        .ifPresent(business ->
                                productEventProducer.publishProductCreated(
                                        ProductCreatedEvent.builder()
                                                .productId(savedProduct.getProductId())
                                                .productName(savedProduct.getProductName())
                                                .productImages(savedProduct.getProductImages())
                                                .productPrice(savedProduct.getProductPrice())
                                                .businessId(business.getBusinessId())
                                                .businessName(business.getBusinessName())
                                                .businessLogo(null)
                                                .businessBanner(null)
                                                .userId(business.getUserId())
                                                .build()
                                )
                        );
            }
        } catch (Exception e) {
            // Log kafka exception
        }

        return savedProduct;
    }

    @Override
    public PageResponse<ProductResponseDTO> getAllproducts(Pageable pageable) {

        Page<Product> page = productRepository.findAll(pageable);

        return new PageResponse<>(
                page.getContent().stream().map(this::toResponseDTO).toList(),
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
    public Product updateProduct(String productId, Product productDetails) {

        Product existingProduct = productRepository
                .findByProductId(productId)
                .orElse(null);

        if (existingProduct == null) {
            return null;
        }

        BusinessAccessState accessState = accessStateRepository
                .findByBusinessId(existingProduct.getBusinessId())
                .orElseGet(() -> {
                    BusinessAccessState state = new BusinessAccessState();
                    state.setBusinessId(existingProduct.getBusinessId());
                    return state;
                });

        boolean isSubscriptionActive = accessState.isSubscriptionActive();

        if (!isSubscriptionActive) {
            boolean attemptingRestrictedChanges =
                    (productDetails.getProductName() != null && !productDetails.getProductName().equals(existingProduct.getProductName())) ||
                    (productDetails.getProductDescription() != null && !productDetails.getProductDescription().equals(existingProduct.getProductDescription())) ||
                    (productDetails.getVisibility() != null && !productDetails.getVisibility().equals(existingProduct.getVisibility())) ||
                    (productDetails.getQuality() != null && !productDetails.getQuality().equals(existingProduct.getQuality())) ||
                    (productDetails.getOrderType() != null && !productDetails.getOrderType().equals(existingProduct.getOrderType())) ||
                    (productDetails.getBadges() != null && !productDetails.getBadges().equals(existingProduct.getBadges())) ||
                    (productDetails.getProductImages() != null && !productDetails.getProductImages().equals(existingProduct.getProductImages()));

            if (attemptingRestrictedChanges) {
                throw new IllegalStateException(
                        "Without an active subscription, only product price and stock status can be updated."
                );
            }
        }

        StringBuilder changes = new StringBuilder();

        if (productDetails.getProductName() != null) {
            existingProduct.setProductName(productDetails.getProductName());
            changes.append("productName,");
        }

        if (productDetails.getProductDescription() != null) {
            existingProduct.setProductDescription(
                    productDetails.getProductDescription());
            changes.append("productDescription,");
        }

        if (productDetails.getProductPrice() != null) {
            existingProduct.setProductPrice(productDetails.getProductPrice());
            changes.append("productPrice,");
        }

        if (productDetails.getStockStatus() != null) {
            existingProduct.setStockStatus(productDetails.getStockStatus());
            changes.append("stockStatus,");
        }

        if (productDetails.getVisibility() != null) {
            existingProduct.setVisibility(productDetails.getVisibility());
            changes.append("visibility,");
        }

        if (productDetails.getQuality() != null) {
            existingProduct.setQuality(productDetails.getQuality());
            changes.append("quality,");
        }

        if (productDetails.getOrderType() != null) {
            existingProduct.setOrderType(productDetails.getOrderType());
            changes.append("orderType,");
        }

        if (productDetails.getBadges() != null) {
            existingProduct.setBadges(productDetails.getBadges());
            changes.append("badges,");
        }

        if (productDetails.getProductImages() != null) {
            existingProduct.setProductImages(productDetails.getProductImages());
            changes.append("productImages,");
        }

        Product savedProduct = productRepository.save(existingProduct);

        if (!changes.isEmpty()) {
            businessRepository.findByBusinessId(savedProduct.getBusinessId())
                    .ifPresent(business ->
                            productEventProducer.publishProductUpdated(
                                    ProductUpdatedEvent.builder()
                                            .productId(savedProduct.getProductId())
                                            .productName(savedProduct.getProductName())
                                            .productImages(savedProduct.getProductImages())
                                            .productPrice(savedProduct.getProductPrice())
                                            .businessId(business.getBusinessId())
                                            .businessName(business.getBusinessName())
                                            .businessLogo(null)
                                            .businessBanner(null)
                                            .userId(business.getUserId())
                                            .changes(changes.toString()
                                                    .replaceAll(",$", ""))
                                            .build()
                            )
                    );
        }

        return savedProduct;
    }

    @Override
    public Optional<Product> getProductById(String productId) {
        return productRepository.findByProductId(productId);
    }

    @Override
    public List<Product> getProductsByName(String productName) {
        return productRepository.findByProductName(productName);
    }

    @Override
    public List<Product> searchProducts(String productName) {
        return productRepository.findByProductNameContaining(productName);
    }

    @Override
    public List<Product> getProductsByBusinessId(String businessId) {
        return productRepository.findByBusinessId(businessId);
    }

    @Override
    public List<Product> getProductsByVisibility(ProductVisibility visibility) {
        return productRepository.findByVisibility(visibility);
    }

    @Override
    public List<Product> getProductsByBadges(String badges) {
        return productRepository.findByBadges(badges);
    }

    @Override
    public List<Product> getProductsByQuality(String quality) {
        return productRepository.findByQuality(quality);
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteProduct(String productId) {

        Product product = productRepository
                .findByProductId(productId)
                .orElse(null);

        if (product == null) {
            return;
        }

        businessRepository.findByBusinessId(product.getBusinessId())
                .ifPresent(business ->
                        productEventProducer.publishProductDeleted(
                                ProductDeletedEvent.builder()
                                        .productId(product.getProductId())
                                        .productName(product.getProductName())
                                        .productImages(product.getProductImages())
                                        .businessId(business.getBusinessId())
                                        .businessName(business.getBusinessName())
                                        .businessLogo(null)
                                        .businessBanner(null)
                                        .userId(business.getUserId())
                                        .build()
                        )
                );

        productRepository.delete(product);
    }

    @Override
    public ProductResponseDTO toResponseDTO(Product product) {

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

    @Override
    public ProductSummaryDTO toSummaryDTO(Product product) {

        if (product == null) {
            return null;
        }

        return new ProductSummaryDTO(
                product.getProductId(),
                product.getProductName(),
                product.getBusinessId(),
                product.getProductPrice(),
                product.getProductDescription()
        );
    }
}