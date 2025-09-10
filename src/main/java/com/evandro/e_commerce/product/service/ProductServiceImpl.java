package com.evandro.e_commerce.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.factory.ProductFactory;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(String name, String description, BigDecimal price) {
        Product product = ProductFactory.create(name, description, price);
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> findProductById(UUID id) {
        return productRepository.findById(id);
    }

    @Override
    public List<Product> listAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> listActiveProducts() {
        return productRepository.findActiveProducts();
    }

    @Override
    public Product updateProduct(UUID id, String name, String description, BigDecimal price) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));

        ProductFactory.create(name, description, price);

        product.update(name, description, price);
        return productRepository.save(product); 
    }

    @Override
    public Product deactivateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));
        product.deactivate(); 
        return productRepository.save(product);
    }

    @Override
    public Product activateProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + id + " not found."));
        product.activate(); 
        return productRepository.save(product);
    }
}
