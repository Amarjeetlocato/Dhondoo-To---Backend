package com.whoami.launch.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.entity.Shop;
import com.whoami.launch.service.CustomerProfileService;
import com.whoami.launch.service.ProductService;
import com.whoami.launch.service.ReelService;
import com.whoami.launch.service.ServiceService;
import com.whoami.launch.service.ShopService;

@RestController
@RequestMapping("/internal/shops")
public class ShopControllerInternal {

    @Autowired
    private ShopService shopService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private ServiceService serviceService;
    
    @Autowired
    private ReelService reelService ;
    
    private CustomerProfileService customerProfileService;


    @GetMapping
    public ResponseEntity<PageResponse<ShopResponseDTO>> getAllShops(
            Pageable pageable) {

        return ResponseEntity.ok(
                shopService.getAllShops(pageable)
        );
    }
    
    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerProfileResponseDTO>> getAllcustomers(
            Pageable pageable) {

        return ResponseEntity.ok(
               customerProfileService.getAllcustomers(pageable)
        );
    }

    @GetMapping("/{shopId}")
    public ResponseEntity<Optional<Shop>> getShop(
            @PathVariable String shopId) {

        return ResponseEntity.ok(
                shopService.getShopById(shopId)
        );
    }
    
    @GetMapping("/products/{shopId}")
    public ResponseEntity<PageResponse<ProductResponseDTO>> getAllproducts(
    		@PathVariable String shopId,
            Pageable pageable) {

        return ResponseEntity.ok(
                productService.getAllproducts(pageable)
        );
    }
    
    @GetMapping("/reels/{shopId}")
    public ResponseEntity<PageResponse<ReelResponseDTO>> getAllreels(
    		@PathVariable String shopId,
            Pageable pageable) {

        return ResponseEntity.ok(
                reelService.getAllreels(pageable)
        );
    }
    
    @GetMapping("/services/{shopId}")
    public ResponseEntity<PageResponse<ServiceResponseDTO>> getAllservices(
    		@PathVariable String shopId,
            Pageable pageable) {

        return ResponseEntity.ok(
                serviceService.getAllservices(pageable)
        );
    }
    
    
}