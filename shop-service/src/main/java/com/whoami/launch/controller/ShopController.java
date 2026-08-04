package com.whoami.launch.controller;

import com.whoami.launch.entity.Shop;
import com.whoami.launch.exception.ResourceNotFoundException;
import com.whoami.launch.dto.ApiResponse;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.dto.ShopStatusUpdateRequest;
import com.whoami.launch.dto.ShopSummaryDTO;
import com.whoami.launch.service.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class ShopController {
    
    @Autowired
    private ShopService shopService;
    
    // internal API endpoints
    @GetMapping("/api/shops")
    public ResponseEntity<List<Shop>> getAllShops() {
        return ResponseEntity.ok(shopService.getAllShops());
    }
    
    @GetMapping("/api/shops/{shopId}")
    public ResponseEntity<Optional<Shop>> getShopById(@PathVariable String shopId) {
        Optional<Shop> shop = shopService.getShopById(shopId);
        if (shop.isPresent()) {
            return ResponseEntity.ok(shop);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/api/shops/search/name/{shopName}")
    public ResponseEntity<Optional<Shop>> getShopByName(@PathVariable String shopName) {
        Optional<Shop> shop = shopService.getShopByName(shopName);
        if (shop.isPresent()) {
            return ResponseEntity.ok(shop);
        }
        return ResponseEntity.notFound().build();
    }
    
    @GetMapping("/api/shops/user/{userId}")
    public ResponseEntity<List<Shop>> getShopsByUserId(@PathVariable String userId) {
        List<Shop> shops = shopService.getShopsByUserId(userId);
        return ResponseEntity.ok(shops);
    }
    
    @GetMapping("/api/shops/search/query")
    public ResponseEntity<List<Shop>> searchShops(@RequestParam String query) {
        List<Shop> shops = shopService.searchShops(query);
        return ResponseEntity.ok(shops);
    }
    
    @PostMapping("/api/shops")
    public ResponseEntity<Shop> createShop(@RequestBody Shop shop) {
        Shop createdShop = shopService.createShop(shop);
        return ResponseEntity.ok(createdShop);
    }
    
	/*
	 * @PutMapping("/api/shops/{shopId}") public ResponseEntity<Shop>
	 * updateShop(@PathVariable String shopId, @RequestBody Shop shopDetails) { Shop
	 * updatedShop = shopService.updateShop(shopId, shopDetails); if (updatedShop !=
	 * null) { return ResponseEntity.ok(updatedShop); } return
	 * ResponseEntity.notFound().build(); }
	 */
    
    @DeleteMapping("/api/shops/{shopId}")
    public ResponseEntity<Void> deleteShop(@PathVariable String shopId) {
        shopService.deleteShop(shopId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/internal-api/shops/{shopId}")
    public ShopResponseDTO getInternalShopById(
            @PathVariable String shopId) {

        Shop shop = shopService.getShopById(shopId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Shop not found"));

        return shopService.toResponseDTO(shop);
    }
    
    @GetMapping("/internal-api/shops/user/{userId}")
    public ResponseEntity<ApiResponse<ShopSummaryDTO>> getInternalShopByUserId(@PathVariable String userId) {
        List<Shop> shops = shopService.getShopsByUserId(userId);
        if (!shops.isEmpty()) {
            ShopSummaryDTO dto = shopService.toSummaryDTO(shops.get(0));
            return ResponseEntity.ok(ApiResponse.success("Shop retrieved", dto));
        }
        return ResponseEntity.ok(ApiResponse.error("Shop not found"));
    }
    
    @GetMapping("/internal-api/shops/exists/{shopId}")
    public ResponseEntity<ApiResponse<Boolean>> checkShopExists(@PathVariable String shopId) {
        Optional<Shop> shop = shopService.getShopById(shopId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", shop.isPresent()));
    }
    
    @GetMapping("/internal-api/shops/exists-by-user/{userId}")
    public ResponseEntity<ApiResponse<Boolean>> checkShopExistsByUserId(@PathVariable String userId) {
        List<Shop> shops = shopService.getShopsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Check completed", !shops.isEmpty()));
    }
    
    @PatchMapping("/api/shops/{shopId}")
    public ResponseEntity<Shop> patchShop(
            @PathVariable String shopId,
            @RequestBody Shop shopDetails
    ) {

        Shop updatedShop =
                shopService.updateShop(
                        shopId,
                        shopDetails
                );

        if (updatedShop != null) {
            return ResponseEntity.ok(
                    updatedShop
            );
        }

        return ResponseEntity.notFound()
                .build();
    }
    
    @PatchMapping("/{shopId}/status")
    public ResponseEntity<ShopResponseDTO> updateShopStatus(
            @PathVariable String shopId,
            @RequestBody ShopStatusUpdateRequest request) {

        Shop shop = shopService.updateShopStatus(
                shopId,
                request.getShopStatus());

        ShopResponseDTO dto = new ShopResponseDTO();
        dto.setShopId(shop.getShopId());
        dto.setShopName(shop.getShopName());
        dto.setStatus(shop.getShopStatus());

        return ResponseEntity.ok(dto);
    }
}

