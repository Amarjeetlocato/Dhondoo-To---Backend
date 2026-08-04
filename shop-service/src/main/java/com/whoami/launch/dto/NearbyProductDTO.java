package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearbyProductDTO {
    private String productId;
    private String productName;
    private String shopId;
    private Double productPrice;
    private String productDescription;
    private Double distance;
}
