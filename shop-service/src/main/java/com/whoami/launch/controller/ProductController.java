package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.entity.Product;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.service.ProductService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {

        return ResponseEntity.ok(
                productService.getAllProducts()
        );
    }

    // ================= GET BY ID =================

    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(
            @PathVariable String productId) {

        return productService
                .getProductById(productId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // ================= GET BY NAME =================

    @GetMapping("/search/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByName(
            @PathVariable String productName) {

        return ResponseEntity.ok(
                productService.getProductsByName(productName)
        );
    }

    // ================= GET BY BUSINESS =================

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Product>> getProductsByBusinessId(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                productService.getProductsByBusinessId(businessId)
        );
    }

    // ================= GET BY VISIBILITY =================

    @GetMapping("/search/visibility/{visibility}")
    public ResponseEntity<List<Product>> getProductsByVisibility(
            @PathVariable ProductVisibility visibility) {

        return ResponseEntity.ok(
                productService.getProductsByVisibility(visibility)
        );
    }

    // ================= GET BY BADGES =================

    @GetMapping("/search/badges/{badges}")
    public ResponseEntity<List<Product>> getProductsByBadges(
            @PathVariable String badges) {

        return ResponseEntity.ok(
                productService.getProductsByBadges(badges)
        );
    }

    // ================= GET BY QUALITY =================

    @GetMapping("/search/quality/{quality}")
    public ResponseEntity<List<Product>> getProductsByQuality(
            @PathVariable String quality) {

        return ResponseEntity.ok(
                productService.getProductsByQuality(quality)
        );
    }

    // ================= SEARCH =================

    @GetMapping("/search/query")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam String query) {

        return ResponseEntity.ok(
                productService.searchProducts(query)
        );
    }

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<Product> createProduct(
            @RequestBody Product product) {

        Product createdProduct =
                productService.createProduct(product);

        return ResponseEntity.ok(createdProduct);
    }

    // ================= UPDATE =================

    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable String productId,
            @RequestBody Product productDetails) {

        Product updatedProduct =
                productService.updateProduct(
                        productId,
                        productDetails
                );

        return ResponseEntity.ok(updatedProduct);
    }

    // ================= DELETE =================

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String productId) {

        productService.deleteProduct(productId);

        return ResponseEntity.noContent().build();
    }

    // ================= INTERNAL - PRODUCT =================

    @GetMapping("/internal/products/{productId}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>>
    getInternalProductById(
            @PathVariable String productId) {

        Product product =
                productService.getProductById(productId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Product not found: " + productId
                                )
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product retrieved",
                        productService.toResponseDTO(product)
                )
        );
    }

    // ================= INTERNAL - BUSINESS =================

    @GetMapping("/internal/products/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>>
    getInternalProductsByBusinessId(
            @PathVariable String businessId) {

        List<Product> products =
                productService.getProductsByBusinessId(businessId);

        if (products.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.error(
                            "No products found"
                    )
            );
        }

        List<ProductSummaryDTO> dtos =
                products.stream()
                        .map(productService::toSummaryDTO)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Products retrieved",
                        dtos
                )
        );
    }

    // ================= INTERNAL - EXISTS =================

    @GetMapping("/internal/products/exists/{productId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkProductExists(
            @PathVariable String productId) {

        boolean exists =
                productService
                        .getProductById(productId)
                        .isPresent();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        exists
                )
        );
    }
}