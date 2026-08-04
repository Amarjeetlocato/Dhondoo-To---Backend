package com.whoami.launch.order.orders.dto;

import java.math.BigDecimal;

public class OrderItemResponse {

    private String productId;
    private String productNameSnapshot;
    private String imageSnapshot;
    private BigDecimal priceSnapshot;
    private Integer quantity;
    private BigDecimal totalPrice;

    public OrderItemResponse() {
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductNameSnapshot() {
        return productNameSnapshot;
    }

    public void setProductNameSnapshot(String productNameSnapshot) {
        this.productNameSnapshot = productNameSnapshot;
    }

    public String getImageSnapshot() {
        return imageSnapshot;
    }

    public void setImageSnapshot(String imageSnapshot) {
        this.imageSnapshot = imageSnapshot;
    }

    public BigDecimal getPriceSnapshot() {
        return priceSnapshot;
    }

    public void setPriceSnapshot(BigDecimal priceSnapshot) {
        this.priceSnapshot = priceSnapshot;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
