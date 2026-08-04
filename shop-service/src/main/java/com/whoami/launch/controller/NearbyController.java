package com.whoami.launch.controller;

import com.whoami.launch.dto.NearbyShopDTO;
import com.whoami.launch.dto.NearbyProductDTO;
import com.whoami.launch.dto.NearbyServiceDTO;
import com.whoami.launch.dto.NearbyReelDTO;
import com.whoami.launch.service.NearbyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/nearby")
public class NearbyController {
    
    @Autowired
    private NearbyService nearbyService;
    
    @GetMapping("/shops")
    public ResponseEntity<List<NearbyShopDTO>> getNearbyShops(
    		
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {
    	System.out.println("shops");
        List<NearbyShopDTO> shops = nearbyService.findNearbyShops(userId, radiusKm, limit);
        return ResponseEntity.ok(shops);
    }
    
    @GetMapping("/products")
    public ResponseEntity<List<NearbyProductDTO>> getNearbyProducts(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {
    	System.out.println("products");
        List<NearbyProductDTO> products = nearbyService.findNearbyProducts(userId, radiusKm, limit);
        return ResponseEntity.ok(products);
    }
    
    @GetMapping("/services")
    public ResponseEntity<List<NearbyServiceDTO>> getNearbyServices(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {
    	System.out.println("services");
        List<NearbyServiceDTO> services = nearbyService.findNearbyServices(userId, radiusKm, limit);
        return ResponseEntity.ok(services);
    }
    
    @GetMapping("/reels")
    public ResponseEntity<List<NearbyReelDTO>> getNearbyReels(
            @RequestParam String userId,
            @RequestParam(defaultValue = "10") Double radiusKm,
            @RequestParam(defaultValue = "20") int limit) {
    	System.out.println("reels");
        List<NearbyReelDTO> reels = nearbyService.findNearbyReels(userId, radiusKm, limit);
        return ResponseEntity.ok(reels);
    }
}
