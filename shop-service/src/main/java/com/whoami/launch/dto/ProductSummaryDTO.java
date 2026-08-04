package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductSummaryDTO {
    private String productId;
    private String productName;
    private String shopId;
    private Double productPrice;
    private String productDescription;
}
