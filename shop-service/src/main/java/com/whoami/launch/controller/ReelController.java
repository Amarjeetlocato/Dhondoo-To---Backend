package com.whoami.launch.controller;

import com.whoami.launch.entity.Reel;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ReelSummaryDTO;
import com.whoami.launch.service.ReelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reels")
public class ReelController {
    
    @Autowired
    private ReelService reelService;
    
    // GET all ree
    @GetMapping
    public ResponseEntity<List<Reel>> getAllReels() {
        return ResponseEntity.ok(reelService.getAllReels());
    }
    
    // GET reel by ID ye wa 
    @GetMapping("/{reelId}")
    public ResponseEntity<Optional<Reel>> getReelById(@PathVariable String reelId) {
        Optional<Reel> reel = reelService.getReelById(reelId);
        if (reel.isPresent()) {
            return ResponseEntity.ok(reel);
        }
        return ResponseEntity.notFound().build();
    }
    
    // GET reels by shop ID
    @GetMapping("/shop/{shopId}")
    public ResponseEntity<List<Reel>> getReelsByShopId(@PathVariable String shopId) {
        List<Reel> reels = reelService.getReelsByShopId(shopId);
        return ResponseEntity.ok(reels);
    }
    
    // GET reels by search query (description)
    @GetMapping("/search/query")
    public ResponseEntity<List<Reel>> searchReels(@RequestParam String query) {
        List<Reel> reels = reelService.searchReels(query);
        return ResponseEntity.ok(reels);
    }
    
    // POST create new reel
    @PostMapping("/create")
    public ResponseEntity<Reel> createReel(@RequestBody Reel reel) {
        Reel createdReel = reelService.createReel(reel);
        return ResponseEntity.ok(createdReel);
    }
    
    // PUT update reel
    @PutMapping("/{reelId}")
    public ResponseEntity<Reel> updateReel(@PathVariable String reelId, @RequestBody Reel reelDetails) {
        Reel updatedReel = reelService.updateReel(reelId, reelDetails);
        if (updatedReel != null) {
            return ResponseEntity.ok(updatedReel);
        }
        return ResponseEntity.notFound().build();
    }
    
    // DELETE reel
    @DeleteMapping("/{reelId}")
    public ResponseEntity<Void> deleteReel(@PathVariable String reelId) {
        reelService.deleteReel(reelId);
        return ResponseEntity.noContent().build();
    }
    
    // Internal API endpoints for Feign clients
    @GetMapping("/internal-api/reels/{reelId}")
    public ResponseEntity<ApiResponse<ReelResponseDTO>> getInternalReelById(@PathVariable String reelId) {
        Optional<Reel> reel = reelService.getReelById(reelId);
        if (reel.isPresent()) {
            ReelResponseDTO dto = reelService.toResponseDTO(reel.get());
            return ResponseEntity.ok(ApiResponse.success("Reel retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Reel not found"));
    }
    
    @GetMapping("/internal-api/reels/shop/{shopId}")
    public ResponseEntity<ApiResponse<List<ReelSummaryDTO>>> getInternalReelsByShopId(@PathVariable String shopId) {
        List<Reel> reels = reelService.getReelsByShopId(shopId);
        if (!reels.isEmpty()) {
            List<ReelSummaryDTO> dtos = reels.stream().map(reelService::toSummaryDTO).toList();
            return ResponseEntity.ok(ApiResponse.success("Reels retrieved", dtos));
        }
        return ResponseEntity.ok(ApiResponse.error("No reels found"));
    }
    
    @GetMapping("/internal-api/reels/exists/{reelId}")
    public ResponseEntity<ApiResponse<Boolean>> checkReelExists(@PathVariable String reelId) {
        Optional<Reel> reel = reelService.getReelById(reelId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", reel.isPresent()));
    }
}
