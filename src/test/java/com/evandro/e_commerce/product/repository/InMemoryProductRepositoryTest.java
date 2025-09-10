package com.evandro.e_commerce.product.repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.model.ProductStatus;

public class InMemoryProductRepositoryTest {

    private InMemoryProductRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryProductRepository();
    }

    @Test
    @DisplayName("Should save a new product")
    void shouldSaveNewProduct() {
        // Arrange
        Product product = new Product("Laptop", "Powerful laptop", new BigDecimal("5000.00"));

        // Act
        Product savedProduct = repository.save(product);

        // Assert
        assertNotNull(savedProduct);
        assertEquals(product.getId(), savedProduct.getId());
        assertEquals(product.getName(), savedProduct.getName());
        assertTrue(repository.findById(product.getId()).isPresent());
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        // Arrange
        Product product = new Product("Monitor", "4K Monitor", new BigDecimal("1500.00"));
        repository.save(product);

        // Act
        Optional<Product> foundProduct = repository.findById(product.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("Should return empty optional if product not found by ID")
    void shouldReturnEmptyOptionalWhenProductNotFound() {
        // Act
        Optional<Product> foundProduct = repository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should return all products")
    void shouldReturnAllProducts() {
        // Arrange
        Product product1 = new Product("Keyboard", "Mechanical", new BigDecimal("300.00"));
        Product product2 = new Product("Mouse", "Gaming mouse", new BigDecimal("200.00"));
        repository.save(product1);
        repository.save(product2);

        // Act
        List<Product> allProducts = repository.findAll();

        // Assert
        assertNotNull(allProducts);
        assertEquals(2, allProducts.size());
        assertTrue(allProducts.contains(product1));
        assertTrue(allProducts.contains(product2));
    }

    @Test
    @DisplayName("Should return only active products")
    void shouldReturnOnlyActiveProducts() {
        // Arrange
        Product activeProduct = new Product("Webcam", "HD Webcam", new BigDecimal("250.00"));
        Product inactiveProduct = new Product("Headset", "Wireless Headset", new BigDecimal("400.00"));
        inactiveProduct.deactivate();
        repository.save(activeProduct);
        repository.save(inactiveProduct);

        // Act
        List<Product> activeProducts = repository.findActiveProducts();

        // Assert
        assertNotNull(activeProducts);
        assertEquals(1, activeProducts.size());
        assertTrue(activeProducts.contains(activeProduct));
        assertFalse(activeProducts.contains(inactiveProduct));
        assertEquals(ProductStatus.ACTIVE, activeProducts.get(0).getStatus());
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateExistingProduct() {
        // Arrange
        Product product = new Product("Old Name", "Old Description", new BigDecimal("100.00"));
        repository.save(product);

        // Act
        product.update("New Name", "New Description", new BigDecimal("120.00"));
        repository.save(product);

        // Assert
        Optional<Product> updatedProduct = repository.findById(product.getId());
        assertTrue(updatedProduct.isPresent());
        assertEquals("New Name", updatedProduct.get().getName());
        assertEquals(new BigDecimal("120.00"), updatedProduct.get().getPrice());
    }
}
