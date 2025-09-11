package com.evandro.e_commerce.order.dto;

import java.math.BigDecimal;
import java.util.UUID;

import com.evandro.e_commerce.order.model.OrderItem;
import com.evandro.e_commerce.product.dto.ProductResponse;

public class OrderItemResponse {

    private final UUID id;
    private final ProductResponse product;
    private final int quantity;
    private final BigDecimal salePrice;
    private final BigDecimal subtotal;

    public OrderItemResponse(OrderItem orderItem) {
        this.id = orderItem.getId();  
        this.product = new ProductResponse(orderItem.getProduct()); 
        this.quantity = orderItem.getQuantity();
        this.salePrice = orderItem.getSalePrice();
        this.subtotal = orderItem.getSubtotal();
    }

    public UUID getId() {
        return id;
    }

    public ProductResponse getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
