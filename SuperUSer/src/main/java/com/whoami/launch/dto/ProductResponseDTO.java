package com.whoami.launch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductResponseDTO {
    
	private String productId;
    private String shopId;
    private String productName;
    private Double productPrice;
    private String productDescription;
    private Long quantity;
    private List<String> productImages;
    private String quality;
    private String orderType;
    private String visibility;
    private String badges;
    
    
    
}
