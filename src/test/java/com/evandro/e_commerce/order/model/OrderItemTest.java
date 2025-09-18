package com.evandro.e_commerce.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.product.model.Product;

public class OrderItemTest {

    @Test
    @DisplayName("Should create order item with valid data")
    void shouldCreateOrderItemWithValidData() {
        // Arrange
        Product product = new Product("Test Product", "Description", new BigDecimal("100.00"));
        int quantity = 2;
        BigDecimal salePrice = new BigDecimal("95.00");

        // Act
        OrderItem orderItem = new OrderItem(product, quantity, salePrice);

        // Assert
        assertNotNull(orderItem);
        assertEquals(product, orderItem.getProduct());
        assertEquals(quantity, orderItem.getQuantity());
        assertEquals(salePrice, orderItem.getSalePrice());
        assertEquals(new BigDecimal("190.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product is null")
    void shouldThrowExceptionWhenProductIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(null, 1, new BigDecimal("10.00"));
        }, "Product cannot be null.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when quantity is zero or less")
    void shouldThrowExceptionWhenQuantityIsInvalid() {
        Product product = new Product("Test Product", "Description", new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(product, 0, new BigDecimal("10.00"));
        }, "Quantity must be greater than zero.");
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(product, -1, new BigDecimal("10.00"));
        }, "Quantity must be greater than zero.");
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when sale price is null or less than or equal to zero")
    void shouldThrowExceptionWhenSalePriceIsInvalid() {
        Product product = new Product("Test Product", "Description", new BigDecimal("100.00"));
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(product, 1, null);
        }, "Sale price must be greater than zero.");
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(product, 1, BigDecimal.ZERO);
        }, "Sale price must be greater than zero.");
        assertThrows(IllegalArgumentException.class, () -> {
            new OrderItem(product, 1, new BigDecimal("-10.00"));
        }, "Sale price must be greater than zero.");
    }

    @Test
    @DisplayName("Should update quantity of order item")
    void shouldUpdateQuantityOfOrderItem() {
        // Arrange
        Product product = new Product("Test Product", "Description", new BigDecimal("100.00"));
        OrderItem orderItem = new OrderItem(product, 1, new BigDecimal("90.00"));
        int newQuantity = 3;

        // Act
        orderItem.updateQuantity(newQuantity);

        // Assert
        assertEquals(newQuantity, orderItem.getQuantity());
        assertEquals(new BigDecimal("270.00"), orderItem.getSubtotal());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when updating quantity to zero or less")
    void shouldThrowExceptionWhenUpdatingQuantityToZeroOrLess() {
        // Arrange
        Product product = new Product("Test Product", "Description", new BigDecimal("100.00"));
        OrderItem orderItem = new OrderItem(product, 1, new BigDecimal("90.00"));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> orderItem.updateQuantity(0));
        assertThrows(IllegalArgumentException.class, () -> orderItem.updateQuantity(-1));
    }
}
