package com.whoami.launch.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.service.impl.AdminShopService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/shops")
@RequiredArgsConstructor
public class AdminShopController {

    private final AdminShopService adminShopService;

    @GetMapping
    public ResponseEntity<PageResponse<ShopResponseDTO>> getAllShops(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                adminShopService.getAllShops(
                        page,
                        size
                )
        );
    }
    
    @GetMapping("/customers")
    public ResponseEntity<PageResponse<CustomerProfileResponseDTO>> getAllCustomers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                adminShopService.getAllCustomers(
                        page,
                        size
                )
        );
    }
    
    @GetMapping("/products/{shopid}")
    public ResponseEntity<PageResponse<ProductResponseDTO>> getAllproducts(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                adminShopService.getAllproducts(
                        shopid,
                        page,
                        size
                )
        );
    }
    
    @GetMapping("/services/{shopid}")
    public ResponseEntity<PageResponse<ServiceResponseDTO>> getAllServices(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                adminShopService.getAllservices(
                        shopid,
                        page,
                        size
                )
        );
    }
    
    @GetMapping("/reels/{shopid}")
    public ResponseEntity<PageResponse<ReelResponseDTO>> getAllreels(
            @PathVariable String shopid,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {

        return ResponseEntity.ok(
                adminShopService.getAllreels(
                        shopid,
                        page,
                        size
                )
        );
    }
    
}