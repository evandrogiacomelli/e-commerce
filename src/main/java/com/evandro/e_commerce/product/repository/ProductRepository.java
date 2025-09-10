package com.evandro.e_commerce.product.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.evandro.e_commerce.product.model.Product;

public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(UUID id);

    List<Product> findAll();

    List<Product> findActiveProducts();
}
