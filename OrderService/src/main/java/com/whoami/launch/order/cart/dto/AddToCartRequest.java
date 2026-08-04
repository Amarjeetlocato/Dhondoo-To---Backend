package com.whoami.launch.order.cart.dto;

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

    @NotBlank(message = "Shop ID is required")
    private String shopId;

    @NotBlank(message = "Product ID is required")
    private String productId;

    @NotBlank(message = "Product name is required")
    private String productName;

    private String imageUrl;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    // Support snapshot field names from frontend
    @JsonProperty("productNameSnapshot")
    private void setProductNameSnapshot(String productNameSnapshot) {
        if (this.productName == null) {
            this.productName = productNameSnapshot;
        }
    }

    @JsonProperty("imageSnapshot")
    private void setImageSnapshot(String imageSnapshot) {
        if (this.imageUrl == null) {
            this.imageUrl = imageSnapshot;
        }
    }

    @JsonProperty("priceSnapshot")
    private void setPriceSnapshot(BigDecimal priceSnapshot) {
        if (this.price == null) {
            this.price = priceSnapshot;
        }
    }

   

}
