package com.whoami.launch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.entity.Product;
import com.whoami.launch.enums.ProductVisibility;

public interface ProductService {

    Product createProduct(Product product);

    PageResponse<ProductResponseDTO> getAllproducts(
            Pageable pageable);

    Product updateProduct(
            String productId,
            Product productDetails);

    Optional<Product> getProductById(
            String productId);

    List<Product> getProductsByName(
            String productName);

    List<Product> searchProducts(
            String productName);

    List<Product> getProductsByBusinessId(
            String businessId);

    List<Product> getProductsByVisibility(
            ProductVisibility visibility);

    List<Product> getProductsByBadges(
            String badges);

    List<Product> getProductsByQuality(
            String quality);

    List<Product> getAllProducts();

    void deleteProduct(
            String productId);

    ProductResponseDTO toResponseDTO(
            Product product);

    ProductSummaryDTO toSummaryDTO(
            Product product);
}