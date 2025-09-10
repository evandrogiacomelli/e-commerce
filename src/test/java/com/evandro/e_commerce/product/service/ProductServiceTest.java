package com.evandro.e_commerce.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.product.exception.InvalidProductDataException;
import com.evandro.e_commerce.product.exception.InvalidProductPriceException;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.model.ProductStatus;
import com.evandro.e_commerce.product.repository.InMemoryProductRepository;
import com.evandro.e_commerce.product.repository.ProductRepository;

public class ProductServiceTest {

    private ProductService productService;
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        productService = new ProductServiceImpl(productRepository);
    }

    @Test
    @DisplayName("Should create and save a new product")
    void shouldCreateAndSaveNewProduct() {
        // Arrange
        String name = "Camera";
        String description = "DSLR Camera";
        BigDecimal price = new BigDecimal("2500.00");

        // Act
        Product createdProduct = productService.createProduct(name, description, price);

        // Assert
        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getId());
        assertEquals(name, createdProduct.getName());
        assertEquals(description, createdProduct.getDescription());
        assertEquals(price, createdProduct.getPrice());
        assertEquals(ProductStatus.ACTIVE, createdProduct.getStatus());

        // Verify if it's in the repository
        assertTrue(productService.findProductById(createdProduct.getId()).isPresent());
    }

    @Test
    @DisplayName("Should throw exception when creating product with invalid data")
    void shouldThrowExceptionWhenCreatingProductWithInvalidData() {
        // Arrange
        String name = null;
        String description = "Invalid Product";
        BigDecimal price = new BigDecimal("10.00");

        // Act & Assert
        assertThrows(InvalidProductDataException.class, () -> {
            productService.createProduct(name, description, price);
        });
    }

    @Test
    @DisplayName("Should find product by ID")
    void shouldFindProductById() {
        // Arrange
        Product product = productService.createProduct("Tablet", "Android Tablet", new BigDecimal("1200.00"));

        // Act
        Optional<Product> foundProduct = productService.findProductById(product.getId());

        // Assert
        assertTrue(foundProduct.isPresent());
        assertEquals(product.getId(), foundProduct.get().getId());
        assertEquals(product.getName(), foundProduct.get().getName());
    }

    @Test
    @DisplayName("Should return empty optional when product not found by ID")
    void shouldReturnEmptyOptionalWhenProductNotFoundById() {
        // Act
        Optional<Product> foundProduct = productService.findProductById(UUID.randomUUID());

        // Assert
        assertFalse(foundProduct.isPresent());
    }

    @Test
    @DisplayName("Should list all products")
    void shouldListAllProducts() {
        // Arrange
        productService.createProduct("Product A", "Desc A", new BigDecimal("10.00"));
        productService.createProduct("Product B", "Desc B", new BigDecimal("20.00"));

        // Act
        List<Product> products = productService.listAllProducts();

        // Assert
        assertNotNull(products);
        assertEquals(2, products.size());
    }

    @Test
    @DisplayName("Should list only active products")
    void shouldListOnlyActiveProducts() {
        // Arrange
        Product activeProduct = productService.createProduct("Active Product", "Desc Active", new BigDecimal("10.00"));
        Product inactiveProduct = productService.createProduct("Inactive Product", "Desc Inactive", new BigDecimal("20.00"));
        productService.deactivateProduct(inactiveProduct.getId());

        // Act
        List<Product> activeProducts = productService.listActiveProducts();

        // Assert
        assertNotNull(activeProducts);
        assertEquals(1, activeProducts.size());
        assertEquals(activeProduct.getId(), activeProducts.get(0).getId());
        assertEquals(ProductStatus.ACTIVE, activeProducts.get(0).getStatus());
    }

    @Test
    @DisplayName("Should update an existing product")
    void shouldUpdateExistingProduct() throws InterruptedException {
        // Arrange
        Product product = productService.createProduct("Old Name", "Old Desc", new BigDecimal("100.00"));
        Thread.sleep(10);
        String newName = "New Name";
        String newDescription = "New Description";
        BigDecimal newPrice = new BigDecimal("150.00");

        // Act
        Product updatedProduct = productService.updateProduct(product.getId(), newName, newDescription, newPrice);

        // Assert
        assertNotNull(updatedProduct);
        assertEquals(product.getId(), updatedProduct.getId());
        assertEquals(newName, updatedProduct.getName());
        assertEquals(newDescription, updatedProduct.getDescription());
        assertEquals(newPrice, updatedProduct.getPrice());
        assertTrue(updatedProduct.getUpdatedAt().isAfter(updatedProduct.getCreatedAt()));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when updating non-existent product")
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(UUID.randomUUID(), "Name", "Desc", new BigDecimal("10.00"));
        });
    }

    @Test
    @DisplayName("Should throw InvalidProductDataException when updating product with invalid data")
    void shouldThrowExceptionWhenUpdatingProductWithInvalidData() {
        // Arrange
        Product product = productService.createProduct("Valid Product", "Valid Desc", new BigDecimal("100.00"));

        // Act & Assert
        assertThrows(InvalidProductPriceException.class, () -> {
            productService.updateProduct(product.getId(), "New Name", "New Desc", BigDecimal.ZERO);
        });
    }

    @Test
    @DisplayName("Should deactivate a product")
    void shouldDeactivateProduct() {
        // Arrange
        Product product = productService.createProduct("Product to Deactivate", "Desc", new BigDecimal("50.00"));

        // Act
        Product deactivatedProduct = productService.deactivateProduct(product.getId());

        // Assert
        assertNotNull(deactivatedProduct);
        assertEquals(ProductStatus.INACTIVE, deactivatedProduct.getStatus());
        // Verify in repository
        Optional<Product> foundProduct = productService.findProductById(product.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(ProductStatus.INACTIVE, foundProduct.get().getStatus());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when deactivating non-existent product")
    void shouldThrowExceptionWhenDeactivatingNonExistentProduct() {
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.deactivateProduct(UUID.randomUUID());
        });
    }

    @Test
    @DisplayName("Should activate a product")
    void shouldActivateProduct() {
        // Arrange
        Product product = productService.createProduct("Product to Activate", "Desc", new BigDecimal("50.00"));
        productService.deactivateProduct(product.getId());

        // Act
        Product activatedProduct = productService.activateProduct(product.getId());

        // Assert
        assertNotNull(activatedProduct);
        assertEquals(ProductStatus.ACTIVE, activatedProduct.getStatus());
        // Verify in repository
        Optional<Product> foundProduct = productService.findProductById(product.getId());
        assertTrue(foundProduct.isPresent());
        assertEquals(ProductStatus.ACTIVE, foundProduct.get().getStatus());
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when activating non-existent product")
    void shouldThrowExceptionWhenActivatingNonExistentProduct() {
        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.activateProduct(UUID.randomUUID());
        });
    }
}
