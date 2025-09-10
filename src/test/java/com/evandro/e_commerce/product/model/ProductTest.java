package com.evandro.e_commerce.product.model;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ProductTest {

    @Test
    @DisplayName("Should create product with valid data")
    void shouldCreateProductWithValidData() {
        // Arrange
        String name = "Smartphone";
        String description = "iPhone 14 Pro Max";
        BigDecimal price = new BigDecimal("7999.99");

        // Act
        Product product = new Product(name, description, price);

        // Assert
        assertNotNull(product);
        assertNotNull(product.getId());
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
        assertNotNull(product.getCreatedAt());
        assertNotNull(product.getUpdatedAt());
        assertEquals(ProductStatus.ACTIVE, product.getStatus());
        assertEquals(product.getCreatedAt(), product.getUpdatedAt()); // No momento da criação, são iguais
    }
}
