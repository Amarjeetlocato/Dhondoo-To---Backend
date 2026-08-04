package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicShopResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.service.PublicShopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/shops")
@RequiredArgsConstructor
public class PublicShopController {

    private final PublicShopService publicShopService;

    @GetMapping("/{slug}")
    public ResponseEntity<PublicShopResponse> getShop(
            @PathVariable String slug) {

        return ResponseEntity.ok(
                publicShopService.getShopBySlug(slug));
    }

    @GetMapping("/{slug}/products")
    public ResponseEntity<PageResponse<ProductResponseDTO>> getProducts(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicShopService.getProducts(slug, page, size));
    }

    @GetMapping("/{slug}/services")
    public ResponseEntity<PageResponse<ServiceResponseDTO>> getServices(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicShopService.getServices(slug, page, size));
    }

    @GetMapping("/{slug}/reels")
    public ResponseEntity<PageResponse<ReelResponseDTO>> getReels(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicShopService.getReels(slug, page, size));
    }

    @GetMapping("/{slug}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>> getReviews(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicShopService.getReviews(slug, page, size));
    }
}