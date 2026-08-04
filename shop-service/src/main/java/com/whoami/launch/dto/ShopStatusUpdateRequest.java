package com.whoami.launch.dto;

import com.locato.enums.ShopStatus;

import lombok.Data;

@Data
public class ShopStatusUpdateRequest {

    private ShopStatus shopStatus;

}