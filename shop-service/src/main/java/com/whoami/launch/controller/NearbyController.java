package com.whoami.launch.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.NearbyBusinessDTO;
import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.dto.NearbyServiceDTO;
import com.whoami.launch.service.NearbyService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/nearby")
@RequiredArgsConstructor
public class NearbyController {

    private final NearbyService nearbyService;

    // ================= NEARBY SHOPS =================

    @GetMapping("/shops")
    public ResponseEntity<List<NearbyBusinessDTO>> getNearbyShops(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {

        return ResponseEntity.ok(
                nearbyService.findNearbyBusiness(
                        userId,
                        radiusKm,
                        limit
                )
        );
    }

    // ================= NEARBY PRODUCTS =================

    @GetMapping("/products")
    public ResponseEntity<List<NearbyProductDTO>> getNearbyProducts(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {

        return ResponseEntity.ok(
                nearbyService.findNearbyProducts(
                        userId,
                        radiusKm,
                        limit
                )
        );
    }

    // ================= NEARBY SERVICES =================

    @GetMapping("/services")
    public ResponseEntity<List<NearbyServiceDTO>> getNearbyServices(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {

        return ResponseEntity.ok(
                nearbyService.findNearbyServices(
                        userId,
                        radiusKm,
                        limit
                )
        );
    }

    // ================= NEARBY REELS =================

    @GetMapping("/reels")
    public ResponseEntity<List<NearbyReelDTO>> getNearbyReels(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {

        return ResponseEntity.ok(
                nearbyService.findNearbyReels(
                        userId,
                        radiusKm,
                        limit
                )
        );
    }
}
