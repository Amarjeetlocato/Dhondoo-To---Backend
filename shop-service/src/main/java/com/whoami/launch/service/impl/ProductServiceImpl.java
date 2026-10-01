package com.whoami.launch.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.entity.Product;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;
import com.whoami.launch.repository.ProductRepository;
import com.whoami.launch.service.ProductService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public Product createProduct(Product product) {

        if (product.getStockStatus() == null) {
            product.setStockStatus(
                    StockStatus.AVAILABLE
            );
        }

        if (product.getVisibility() == null) {
            product.setVisibility(
                    ProductVisibility.PUBLIC
            );
        }

        return productRepository.save(product);
    }

    @Override
    public PageResponse<ProductResponseDTO> getAllproducts(
            Pageable pageable) {

        Page<Product> page =
                productRepository.findAll(pageable);

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

    @Override
    public Product updateProduct(
            String productId,
            Product productDetails) {

        Product existingProduct =
                productRepository
                        .findByProductId(productId)
                        .orElse(null);

        if (existingProduct == null) {
            return null;
        }

        if (productDetails.getProductName() != null) {
            existingProduct.setProductName(
                    productDetails.getProductName()
            );
        }

        if (productDetails.getProductDescription() != null) {
            existingProduct.setProductDescription(
                    productDetails.getProductDescription()
            );
        }

        if (productDetails.getProductPrice() != null) {
            existingProduct.setProductPrice(
                    productDetails.getProductPrice()
            );
        }

        if (productDetails.getStockStatus() != null) {
            existingProduct.setStockStatus(
                    productDetails.getStockStatus()
            );
        }

        if (productDetails.getVisibility() != null) {
            existingProduct.setVisibility(
                    productDetails.getVisibility()
            );
        }

        if (productDetails.getQuality() != null) {
            existingProduct.setQuality(
                    productDetails.getQuality()
            );
        }

        if (productDetails.getOrderType() != null) {
            existingProduct.setOrderType(
                    productDetails.getOrderType()
            );
        }

        if (productDetails.getBadges() != null) {
            existingProduct.setBadges(
                    productDetails.getBadges()
            );
        }

        if (productDetails.getProductImages() != null) {
            existingProduct.setProductImages(
                    productDetails.getProductImages()
            );
        }

        return productRepository.save(existingProduct);
    }

    @Override
    public Optional<Product> getProductById(
            String productId) {

        return productRepository.findByProductId(
                productId
        );
    }

    @Override
    public List<Product> getProductsByName(
            String productName) {

        return productRepository.findByProductName(
                productName
        );
    }

    @Override
    public List<Product> searchProducts(
            String productName) {

        return productRepository
                .findByProductNameContaining(
                        productName
                );
    }

    @Override
    public List<Product> getProductsByBusinessId(
            String businessId) {

        return productRepository.findByBusinessId(
                businessId
        );
    }

    @Override
    public List<Product> getProductsByVisibility(
            ProductVisibility visibility) {

        return productRepository.findByVisibility(
                visibility
        );
    }

    @Override
    public List<Product> getProductsByBadges(
            String badges) {

        return productRepository.findByBadges(
                badges
        );
    }

    @Override
    public List<Product> getProductsByQuality(
            String quality) {

        return productRepository.findByQuality(
                quality
        );
    }

    @Override
    public List<Product> getAllProducts() {

        return productRepository.findAll();
    }

    @Override
    public void deleteProduct(
            String productId) {

        Product product =
                productRepository
                        .findByProductId(productId)
                        .orElse(null);

        if (product != null) {
            productRepository.delete(product);
        }
    }

    @Override
    public ProductResponseDTO toResponseDTO(
            Product product) {

        if (product == null) {
            return null;
        }

        return new ProductResponseDTO(
                product.getProductId(),
                product.getBusinessId(),
                product.getProductName(),
                product.getProductPrice(),
                product.getProductDescription(),
                product.getStockStatus(),
                product.getProductImages(),
                product.getOrderType(),
                product.getBadges(),
                product.getVisibility(),
                product.getQuality()
        );
    }

    @Override
    public ProductSummaryDTO toSummaryDTO(
            Product product) {

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