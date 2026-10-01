package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.PublicBusinessResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReviewResponse;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.service.PublicBusinessService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/public/businesses")
@RequiredArgsConstructor
public class PublicBusinessController {

    private final PublicBusinessService publicBusinessService;

    // ================= PUBLIC BUSINESS =================

    @GetMapping("/{slug}")
    public ResponseEntity<PublicBusinessResponse> getBusiness(
            @PathVariable String slug) {

        return ResponseEntity.ok(
                publicBusinessService.getBusinessBySlug(slug)
        );
    }

    // ================= PRODUCTS =================

    @GetMapping("/{slug}/products")
    public ResponseEntity<PageResponse<ProductResponseDTO>>
    getProducts(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicBusinessService.getProducts(
                        slug,
                        page,
                        size
                )
        );
    }

    // ================= SERVICES =================

    @GetMapping("/{slug}/services")
    public ResponseEntity<PageResponse<ServiceResponseDTO>>
    getServices(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicBusinessService.getServices(
                        slug,
                        page,
                        size
                )
        );
    }

    // ================= REELS =================

    @GetMapping("/{slug}/reels")
    public ResponseEntity<PageResponse<ReelResponseDTO>>
    getReels(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicBusinessService.getReels(
                        slug,
                        page,
                        size
                )
        );
    }

    // ================= REVIEWS =================

    @GetMapping("/{slug}/reviews")
    public ResponseEntity<PageResponse<ReviewResponse>>
    getReviews(
            @PathVariable String slug,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        return ResponseEntity.ok(
                publicBusinessService.getReviews(
                        slug,
                        page,
                        size
                )
        );
    }
}