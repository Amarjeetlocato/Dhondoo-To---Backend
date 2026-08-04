package com.whoami.launch.service.impl;

import org.springframework.stereotype.Service;

import com.whoami.launch.dto.CustomerProfileResponseDTO;
import com.whoami.launch.dto.PageResponse;
import com.whoami.launch.dto.ProductResponseDTO;
import com.whoami.launch.dto.ReelResponseDTO;
import com.whoami.launch.dto.ServiceResponseDTO;
import com.whoami.launch.dto.ShopResponseDTO;
import com.whoami.launch.feign.ShopFeignClient;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminShopService {

    private final ShopFeignClient shopFeignClient;

    public PageResponse<ShopResponseDTO> getAllShops(
            int page,
            int size
    ) {

        return shopFeignClient.getAllShops(
                page,
                size
        );
    }
    
    public PageResponse<CustomerProfileResponseDTO> getAllCustomers(
            int page,
            int size
    ) {

        return shopFeignClient.getAllCustomers(
                page,
                size
        );
    }
    
    public PageResponse<ProductResponseDTO> getAllproducts(
    		String shopid,
            int page,
            int size
    ) {

        return shopFeignClient.getAllproducts(
                shopid,
                page,
                size
        );
    }
    
    public PageResponse<ReelResponseDTO> getAllreels(
    		String shopid,
            int page,
            int size
    ) {

        return shopFeignClient.getAllreels(
                shopid,
                page,
                size
        );
    }
    
    public PageResponse<ServiceResponseDTO> getAllservices(
    		String shopid,
            int page,
            int size
    ) {

        return shopFeignClient.getAllservices(
                shopid,
                page,
                size
        );
    }
}