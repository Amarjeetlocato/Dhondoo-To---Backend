package com.whoami.launch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.whoami.launch.enums.ProductVisibility;
import com.whoami.launch.enums.StockStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {

    private String productId;

    private String businessId;
 

    private String productName;
    private Double productPrice;
    private String productDescription;

    private StockStatus stock;

    private List<String> productImages;

    private String quality;
    private String orderType;

    private ProductVisibility visibility;

    private String badges;
}