package com.evandro.e_commerce.order.model;

import java.math.BigDecimal;
import java.util.UUID;

import com.evandro.e_commerce.product.model.Product;

public class OrderItem {

    private UUID id;
    private Product product; 
    private int quantity;
    private BigDecimal salePrice;

    public OrderItem(Product product, int quantity, BigDecimal salePrice) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null.");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Sale price must be greater than zero.");
        }

        this.id = UUID.randomUUID();
        this.product = product;
        this.quantity = quantity;
        this.salePrice = salePrice;
    }

    public UUID getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public BigDecimal getSubtotal() {
        return salePrice.multiply(BigDecimal.valueOf(quantity));
    }

    public void updateQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
        this.quantity = newQuantity;
    }
}
