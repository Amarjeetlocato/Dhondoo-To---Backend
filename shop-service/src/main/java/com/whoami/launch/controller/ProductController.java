package com.whoami.launch.controller;

import com.whoami.launch.entity.Product;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ProductSummaryDTO;
import com.whoami.launch.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService productService;
    
    // GET all products
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }
    
    // GET product by ID
    @GetMapping("/{productId}")
    public ResponseEntity<Optional<Product>> getProductById(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        if (product.isPresent()) {
            return ResponseEntity.ok(product);
        }
        return ResponseEntity.notFound().build();
    }
    
    // GET products by name
    @GetMapping("/search/name/{productName}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable String productName) {
        List<Product> products = productService.getProductsByName(productName);
        return ResponseEntity.ok(products);
    }
    
    // GET products by shop ID
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Product>> getProductsByShopId(@PathVariable String shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        return ResponseEntity.ok(products);
    }
    
    // GET products by visibility
    @GetMapping("/search/visibility/{visibility}")
    public ResponseEntity<List<Product>> getProductsByVisibility(@PathVariable ProductVisibility visibility) {
        List<Product> products = productService.getProductsByVisibility(visibility);
        return ResponseEntity.ok(products);
    }
    
    // GET products by badges
    @GetMapping("/search/badges/{badges}")
    public ResponseEntity<List<Product>> getProductsByBadges(@PathVariable String badges) {
        List<Product> products = productService.getProductsByBadges(badges);
        return ResponseEntity.ok(products);
    }
    
    // GET products by quality
    @GetMapping("/search/quality/{quality}")
    public ResponseEntity<List<Product>> getProductsByQuality(@PathVariable String quality) {
        List<Product> products = productService.getProductsByQuality(quality);
        return ResponseEntity.ok(products);
    }
    
    // GET products by search query
    @GetMapping("/search/query")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam String query) {
        List<Product> products = productService.searchProducts(query);
        return ResponseEntity.ok(products);
    }
    
    // POST create new product
    @PostMapping("/create")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
    	System.out.print("product creating..");
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(createdProduct);
    }
    
    // PUT update product
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(@PathVariable String productId, @RequestBody Product productDetails) {
        Product updatedProduct = productService.updateProduct(productId, productDetails);
        if (updatedProduct != null) {
            return ResponseEntity.ok(updatedProduct);
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE product
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/internal-api/products/{productId}")
    public ProductResponseDTO getInternalProductById(
            @PathVariable String productId) {

        Product product = productService.getProductById(productId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found"));

        return productService.toResponseDTO(product);
    }
    
    @GetMapping("/internal-api/products/shop/{shopId}")
    public ResponseEntity<ApiResponse<List<ProductSummaryDTO>>> getInternalProductsByShopId(@PathVariable String shopId) {
        List<Product> products = productService.getProductsByShopId(shopId);
        if (!products.isEmpty()) {
            List<ProductSummaryDTO> dtos = products.stream().map(productService::toSummaryDTO).toList();
            return ResponseEntity.ok(ApiResponse.success("Products retrieved", dtos));
        }
        return ResponseEntity.ok(ApiResponse.error("No products found"));
    }
    
    @GetMapping("/internal-api/products/exists/{productId}")
    public ResponseEntity<ApiResponse<Boolean>> checkProductExists(@PathVariable String productId) {
        Optional<Product> product = productService.getProductById(productId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", product.isPresent()));
    }
}
