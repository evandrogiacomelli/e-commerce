package com.evandro.e_commerce.product.factory;

import java.math.BigDecimal;

import com.evandro.e_commerce.product.exception.InvalidProductDataException;
import com.evandro.e_commerce.product.exception.InvalidProductPriceException;
import com.evandro.e_commerce.product.model.Product;

public class ProductFactory {

    public static Product create(String name, String description, BigDecimal price) {
        validateInputs(name, description, price);
        return new Product(name, description, price);
    }

    private static void validateInputs(String name, String description, BigDecimal price) {
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
}
