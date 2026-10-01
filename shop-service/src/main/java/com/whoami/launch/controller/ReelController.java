package com.whoami.launch.controller;

import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.entity.Reel;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.service.ReelService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reels")
@RequiredArgsConstructor
public class ReelController {

    private final ReelService reelService;

    // ================= GET ALL =================

    @GetMapping
    public ResponseEntity<List<Reel>> getAllReels() {

        return ResponseEntity.ok(
                reelService.getAllReels()
        );
    }

    // ================= GET BY ID =================

    @GetMapping("/{reelId}")
    public ResponseEntity<Reel> getReelById(
            @PathVariable String reelId) {

        return reelService
                .getReelById(reelId)
                .map(ResponseEntity::ok)
                .orElseGet(() ->
                        ResponseEntity.notFound().build()
                );
    }

    // ================= GET BY BUSINESS =================

    @GetMapping("/business/{businessId}")
    public ResponseEntity<List<Reel>> getReelsByBusinessId(
            @PathVariable String businessId) {

        return ResponseEntity.ok(
                reelService.getReelsByBusinessId(
                        businessId
                )
        );
    }

    // ================= SEARCH =================

    @GetMapping("/search/query")
    public ResponseEntity<List<Reel>> searchReels(
            @RequestParam String query) {

        return ResponseEntity.ok(
                reelService.searchReels(query)
        );
    }

    // ================= CREATE =================

    @PostMapping
    public ResponseEntity<Reel> createReel(
            @RequestBody Reel reel) {

        return ResponseEntity.ok(
                reelService.createReel(reel)
        );
    }

    // ================= UPDATE =================

    @PutMapping("/{reelId}")
    public ResponseEntity<Reel> updateReel(
            @PathVariable String reelId,
            @RequestBody Reel reelDetails) {

        Reel updatedReel =
                reelService.updateReel(
                        reelId,
                        reelDetails
                );

        return ResponseEntity.ok(updatedReel);
    }

    // ================= DELETE =================

    @DeleteMapping("/{reelId}")
    public ResponseEntity<Void> deleteReel(
            @PathVariable String reelId) {

        reelService.deleteReel(reelId);

        return ResponseEntity.noContent().build();
    }

    // ================= INTERNAL - REEL =================

    @GetMapping("/internal/reels/{reelId}")
    public ResponseEntity<ApiResponse<ReelResponseDTO>>
    getInternalReelById(
            @PathVariable String reelId) {

        Reel reel =
                reelService.getReelById(reelId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Reel not found: " + reelId
                                )
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Reel retrieved",
                        reelService.toResponseDTO(reel)
                )
        );
    }

    // ================= INTERNAL - BUSINESS =================

    @GetMapping("/internal/reels/business/{businessId}")
    public ResponseEntity<ApiResponse<List<ReelSummaryDTO>>>
    getInternalReelsByBusinessId(
            @PathVariable String businessId) {

        List<Reel> reels =
                reelService.getReelsByBusinessId(
                        businessId
                );

        if (reels.isEmpty()) {
            return ResponseEntity.ok(
                    ApiResponse.error(
                            "No reels found"
                    )
            );
        }

        List<ReelSummaryDTO> dtos =
                reels.stream()
                        .map(reelService::toSummaryDTO)
                        .toList();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Reels retrieved",
                        dtos
                )
        );
    }

    // ================= INTERNAL - EXISTS =================

    @GetMapping("/internal/reels/exists/{reelId}")
    public ResponseEntity<ApiResponse<Boolean>>
    checkReelExists(
            @PathVariable String reelId) {

        boolean exists =
                reelService
                        .getReelById(reelId)
                        .isPresent();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Check completed",
                        exists
                )
        );
    }
}