package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductSummaryDTO {

    private String productId;

    private String productName;

    private String businessId;


    private Double productPrice;
    private String productDescription;
}
