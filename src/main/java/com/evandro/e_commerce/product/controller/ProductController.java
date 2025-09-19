package com.evandro.e_commerce.product.controller;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evandro.e_commerce.product.dto.ProductRequest;
import com.evandro.e_commerce.product.dto.ProductResponse;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        Product product = productService.createProduct(request.getName(), request.getDescription(), request.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ProductResponse(product));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable UUID id) {
        return productService.findProductById(id)
                .map(ProductResponse::new)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> products = productService.listAllProducts().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ProductResponse>> getActiveProducts() {
        List<ProductResponse> products = productService.listActiveProducts().stream()
                .map(ProductResponse::new)
                .collect(Collectors.toList());
        return ResponseEntity.ok(products);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable UUID id, @RequestBody ProductRequest request) {
        Product updatedProduct = productService.updateProduct(id, request.getName(), request.getDescription(), request.getPrice());
        return ResponseEntity.ok(new ProductResponse(updatedProduct));
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<ProductResponse> deactivateProduct(@PathVariable UUID id) {
        Product deactivatedProduct = productService.deactivateProduct(id);
        return ResponseEntity.ok(new ProductResponse(deactivatedProduct));
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<ProductResponse> activateProduct(@PathVariable UUID id) {
        Product activatedProduct = productService.activateProduct(id);
        return ResponseEntity.ok(new ProductResponse(activatedProduct));
    }
}
