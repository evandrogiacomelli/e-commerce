package com.evandro.e_commerce.order.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.evandro.e_commerce.order.model.Order;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findAll();

    List<Order> findByCustomerId(UUID customerId);
}
