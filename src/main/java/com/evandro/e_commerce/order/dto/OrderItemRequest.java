package com.evandro.e_commerce.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItemRequest {

    private UUID productId;
    private int quantity;
    private BigDecimal salePrice; 

    public OrderItemRequest() {
    } 

    public OrderItemRequest(UUID productId, int quantity, BigDecimal salePrice) {
        this.productId = productId;
        this.quantity = quantity;
        this.salePrice = salePrice;
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setProductId(UUID productId) {
        this.productId = productId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }
}
