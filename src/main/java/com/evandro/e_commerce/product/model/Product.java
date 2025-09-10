package com.evandro.e_commerce.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public class Product {

    private final UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private ProductStatus status;

    public Product(String name, String description, BigDecimal price) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.status = ProductStatus.ACTIVE;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void update(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.updatedAt = LocalDateTime.now();
    }

    public void deactivate() {
        this.status = ProductStatus.INACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        this.status = ProductStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }
}
