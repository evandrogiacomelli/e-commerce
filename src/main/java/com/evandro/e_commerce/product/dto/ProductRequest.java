package com.evandro.e_commerce.product.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductRequest {

    @NotBlank(message = "The name cannot be empty or null.")
    @Size(min = 3, max = 100, message = "The name must be between 3 and 100 characters long.")
    private String name;
    private String description;
    @NotNull(message = "The price cannot be zero.")
    @Positive(message = "The price must be a positive value.")
    private BigDecimal price;

    public ProductRequest() {
    }

    public ProductRequest(String name, String description, BigDecimal price) {
        this.name = name;
        this.description = description;
        this.price = price;
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

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
