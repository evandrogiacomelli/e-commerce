package com.evandro.e_commerce.order.model;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.*;
import com.evandro.e_commerce.product.model.Product;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "sale_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal salePrice;

    public OrderItem() {}

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

        this.product = product;
        this.quantity = quantity;
        this.salePrice = salePrice;
    }

    public UUID getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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
