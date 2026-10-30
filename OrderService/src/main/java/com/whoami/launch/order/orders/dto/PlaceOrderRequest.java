package com.whoami.launch.order.orders.dto;

import jakarta.validation.constraints.NotBlank;

public class PlaceOrderRequest {

    private String deliveryAddress;

    private String customerNote;

    private String fulfillmentType; // "HOME_DELIVERY" or "SELF_PICKUP"

    private java.math.BigDecimal deliveryFee;

    private Double customerLat;

    private Double customerLng;

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getCustomerNote() {
        return customerNote;
    }

    public void setCustomerNote(String customerNote) {
        this.customerNote = customerNote;
    }

    public String getFulfillmentType() {
        return fulfillmentType;
    }

    public void setFulfillmentType(String fulfillmentType) {
        this.fulfillmentType = fulfillmentType;
    }

    public java.math.BigDecimal getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(java.math.BigDecimal deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public Double getCustomerLat() {
        return customerLat;
    }

    public void setCustomerLat(Double customerLat) {
        this.customerLat = customerLat;
    }

    public Double getCustomerLng() {
        return customerLng;
    }

    public void setCustomerLng(Double customerLng) {
        this.customerLng = customerLng;
    }
}
