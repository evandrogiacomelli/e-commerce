package com.evandro.e_commerce.product.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.evandro.e_commerce.product.dto.ProductRequest;
import com.evandro.e_commerce.product.exception.InvalidProductDataException;
import com.evandro.e_commerce.product.exception.InvalidProductPriceException;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.model.ProductStatus;
import com.evandro.e_commerce.product.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("Should create a new product and return 201 CREATED")
    void shouldCreateProduct() throws Exception {
        // Arrange
        ProductRequest request = new ProductRequest("Smartphone", "Latest model", new BigDecimal("1500.00"));
        Product mockProduct = new Product("Smartphone", "Latest model", new BigDecimal("1500.00"));
        when(productService.createProduct(anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(mockProduct);

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Smartphone"))
                .andExpect(jsonPath("$.price").value(1500.00));

        verify(productService, times(1)).createProduct(anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when creating product with invalid data")
    void shouldReturnBadRequestWhenCreatingProductWithInvalidData() throws Exception {
        // Arrange
        String invalidName = null; 
        String description = "Invalid product";
        BigDecimal price = new BigDecimal("100.00");
        ProductRequest request = new ProductRequest(invalidName, description, price);

        when(productService.createProduct(eq(invalidName), eq(description), eq(price)))
                .thenThrow(new InvalidProductDataException("Name cannot be null"));

        // Act & Assert
        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).createProduct(eq(invalidName), eq(description), eq(price));
    }

    @Test
    @DisplayName("Should get product by ID and return 200 OK")
    void shouldGetProductById() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product mockProduct = new Product("Laptop", "Gaming Laptop", new BigDecimal("8000.00"));
        when(productService.findProductById(productId)).thenReturn(Optional.of(mockProduct));

        // Act & Assert
        mockMvc.perform(get("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(mockProduct.getId().toString()))
                .andExpect(jsonPath("$.name").value("Laptop"));

        verify(productService, times(1)).findProductById(productId);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when product ID does not exist")
    void shouldReturnNotFoundWhenProductIdDoesNotExist() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productService.findProductById(nonExistentId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/products/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).findProductById(nonExistentId);
    }

    @Test
    @DisplayName("Should get all products and return 200 OK")
    void shouldGetAllProducts() throws Exception {
        // Arrange
        Product product1 = new Product("Product A", "Desc A", new BigDecimal("10.00"));
        Product product2 = new Product("Product B", "Desc B", new BigDecimal("20.00"));
        List<Product> mockProducts = Arrays.asList(product1, product2);
        when(productService.listAllProducts()).thenReturn(mockProducts);

        // Act & Assert
        mockMvc.perform(get("/products")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Product A"));

        verify(productService, times(1)).listAllProducts();
    }

    @Test
    @DisplayName("Should get active products and return 200 OK")
    void shouldGetActiveProducts() throws Exception {
        // Arrange
        Product activeProduct = new Product("Active Product", "Desc Active", new BigDecimal("100.00"));
        activeProduct.activate();
        List<Product> mockActiveProducts = Arrays.asList(activeProduct);
        when(productService.listActiveProducts()).thenReturn(mockActiveProducts);

        // Act & Assert
        mockMvc.perform(get("/products/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Active Product"));

        verify(productService, times(1)).listActiveProducts();
    }

    @Test
    @DisplayName("Should update an existing product and return 200 OK")
    void shouldUpdateProduct() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Updated Name", "Updated Desc", new BigDecimal("200.00"));
        Product updatedMockProduct = new Product("Updated Name", "Updated Desc", new BigDecimal("200.00"));
        when(productService.updateProduct(eq(productId), anyString(), anyString(), any(BigDecimal.class)))
                .thenReturn(updatedMockProduct);

        // Act & Assert
        mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.price").value(200.00));

        verify(productService, times(1)).updateProduct(eq(productId), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when updating non-existent product")
    void shouldReturnNotFoundWhenUpdatingNonExistentProduct() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Name", "Desc", new BigDecimal("10.00"));
        when(productService.updateProduct(eq(nonExistentId), anyString(), anyString(), any(BigDecimal.class)))
                .thenThrow(new ProductNotFoundException("Product not found"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).updateProduct(eq(nonExistentId), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when updating product with invalid data")
    void shouldReturnBadRequestWhenUpdatingProductWithInvalidData() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductRequest request = new ProductRequest("Name", "Desc", BigDecimal.ZERO);
        when(productService.updateProduct(eq(productId), anyString(), anyString(), any(BigDecimal.class)))
                .thenThrow(new InvalidProductPriceException("Price must be greater than zero"));

        // Act & Assert
        mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        verify(productService, times(1)).updateProduct(eq(productId), anyString(), anyString(), any(BigDecimal.class));
    }

    @Test
    @DisplayName("Should deactivate a product and return 200 OK")
    void shouldDeactivateProduct() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product deactivatedMockProduct = new Product("Product", "Desc", new BigDecimal("100.00"));
        deactivatedMockProduct.deactivate();
        when(productService.deactivateProduct(productId)).thenReturn(deactivatedMockProduct);

        // Act & Assert
        mockMvc.perform(patch("/products/{id}/deactivate", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ProductStatus.INACTIVE.toString()));

        verify(productService, times(1)).deactivateProduct(productId);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deactivating non-existent product")
    void shouldReturnNotFoundWhenDeactivatingNonExistentProduct() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productService.deactivateProduct(nonExistentId)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act & Assert
        mockMvc.perform(patch("/products/{id}/deactivate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).deactivateProduct(nonExistentId);
    }

    @Test
    @DisplayName("Should activate a product and return 200 OK")
    void shouldActivateProduct() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        Product activatedMockProduct = new Product("Product", "Desc", new BigDecimal("100.00"));
        activatedMockProduct.activate();
        when(productService.activateProduct(productId)).thenReturn(activatedMockProduct);

        // Act & Assert
        mockMvc.perform(patch("/products/{id}/activate", productId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(ProductStatus.ACTIVE.toString()));

        verify(productService, times(1)).activateProduct(productId);
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when activating non-existent product")
    void shouldReturnNotFoundWhenActivatingNonExistentProduct() throws Exception {
        // Arrange
        UUID nonExistentId = UUID.randomUUID();
        when(productService.activateProduct(nonExistentId)).thenThrow(new ProductNotFoundException("Product not found"));

        // Act & Assert
        mockMvc.perform(patch("/products/{id}/activate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(productService, times(1)).activateProduct(nonExistentId);
    }
}
