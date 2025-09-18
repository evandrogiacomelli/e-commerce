package com.evandro.e_commerce.product.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.model.ProductStatus;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    @Query("SELECT p FROM Product p WHERE p.status = :status")
    List<Product> findByStatus(ProductStatus status);

    default List<Product> findActiveProducts() {
        return findByStatus(ProductStatus.ACTIVE);
    }
}
