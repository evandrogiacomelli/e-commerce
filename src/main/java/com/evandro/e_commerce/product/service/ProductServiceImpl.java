package com.evandro.e_commerce.product.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.evandro.e_commerce.product.exception.InvalidProductDataException;
import com.evandro.e_commerce.product.exception.InvalidProductPriceException;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    private void validateProductInputs(String name, String description, BigDecimal price) {
            if (name == null || name.trim().isEmpty()) {
                throw new InvalidProductDataException("Product name cannot be null or empty.");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new InvalidProductDataException("Product description cannot be null or empty.");
            }
            if (price == null || price.compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidProductPriceException("Product price must be greater than zero.");
            }
        }

    @Override
    public Product createProduct(String name, String description, BigDecimal price) {
        validateProductInputs(name, description, price);
        Product product = new Product(name, description, price);
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

        validateProductInputs(name, description, price);

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
