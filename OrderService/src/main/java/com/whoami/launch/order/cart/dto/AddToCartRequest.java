package com.whoami.launch.order.cart.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {

    @JsonProperty("businessId")
    @JsonAlias({"businessId", "shopId"})
    @NotBlank(message = "Business ID is required")
    private String businessId;

    @JsonProperty("productId")
    @JsonAlias({"productId", "id"})
    @NotBlank(message = "Product ID is required")
    private String productId;

    @JsonProperty("productName")
    @JsonAlias({"productName", "productNameSnapshot", "name"})
    @NotBlank(message = "Product name is required")
    private String productName;

    @JsonProperty("imageUrl")
    @JsonAlias({"imageUrl", "imageSnapshot", "image"})
    private String imageUrl;

    @JsonProperty("price")
    @JsonAlias({"price", "priceSnapshot", "productPrice"})
    @NotNull(message = "Price is required")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Price must be greater than 0"
    )
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(
            value = 1,
            message = "Quantity must be at least 1"
    )
    private Integer quantity;
}
