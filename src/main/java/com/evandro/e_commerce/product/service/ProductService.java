package com.evandro.e_commerce.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.evandro.e_commerce.product.model.Product;

public interface ProductService {

    Product createProduct(String name, String description, BigDecimal price);

    Optional<Product> findProductById(UUID id);

    List<Product> listAllProducts();

    List<Product> listActiveProducts();

    Product updateProduct(UUID id, String name, String description, BigDecimal price);

    Product deactivateProduct(UUID id);

    Product activateProduct(UUID id);
}
