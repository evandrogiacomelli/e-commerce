package com.evandro.e_commerce.product.factory;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.product.exception.InvalidProductDataException;
import com.evandro.e_commerce.product.exception.InvalidProductPriceException;
import com.evandro.e_commerce.product.model.Product;

public class ProductFactoryTest {

    @Test
    @DisplayName("Should create product with valid data using factory")
    void shouldCreateProductWithValidDataUsingFactory() {
        // Arrange
        String name = "Laptop";
        String description = "Dell XPS 15";
        BigDecimal price = new BigDecimal("10000.00");

        // Act
        Product product = ProductFactory.create(name, description, price);

        // Assert
        assertNotNull(product);
        assertEquals(name, product.getName());
        assertEquals(description, product.getDescription());
        assertEquals(price, product.getPrice());
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when name is null")
    void shouldThrowExceptionWhenNameIsNull() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create(null, "Description", new BigDecimal("100.00"));
        }, "Product name cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when name is empty")
    void shouldThrowExceptionWhenNameIsEmpty() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create("", "Description", new BigDecimal("100.00"));
        }, "Product name cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when name is blank")
    void shouldThrowExceptionWhenNameIsBlank() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create("   ", "Description", new BigDecimal("100.00"));
        }, "Product name cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when description is null")
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create("Product Name", null, new BigDecimal("100.00"));
        }, "Product description cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when description is empty")
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create("Product Name", "", new BigDecimal("100.00"));
        }, "Product description cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when description is blank")
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        assertThrows(InvalidProductDataException.class, () -> {
            ProductFactory.create("Product Name", "   ", new BigDecimal("100.00"));
        }, "Product description cannot be null or empty.");
    }

    @Test
    @DisplayName("Should throw InvalidProductPriceException when price is null")
    void shouldThrowExceptionWhenPriceIsNull() {
        assertThrows(InvalidProductPriceException.class, () -> {
            ProductFactory.create("Product", "Description", null);
        }, "Product price must be greater than zero.");
    }

    @Test
    @DisplayName("Should throw InvalidProductPriceException when price is zero")
    void shouldThrowExceptionWhenPriceIsZero() {
        assertThrows(InvalidProductPriceException.class, () -> {
            ProductFactory.create("Product", "Description", BigDecimal.ZERO);
        }, "Product price must be greater than zero.");
    }

    @Test
    @DisplayName("Should throw InvalidProductPriceException when price is negative")
    void shouldThrowExceptionWhenPriceIsNegative() {
        assertThrows(InvalidProductPriceException.class, () -> {
            ProductFactory.create("Product", "Description", new BigDecimal("-10.00"));
        }, "Product price must be greater than zero.");
    }
}
