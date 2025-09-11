package com.evandro.e_commerce.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.evandro.e_commerce.order.model.Order;

public interface OrderService {

    // Order createOrder(UUID customerId); 

    Optional<Order> findOrderById(UUID orderId);

    List<Order> listAllOrders();

    List<Order> listOrdersByCustomerId(UUID customerId);

    Order addItemToOrder(UUID orderId, UUID productId, int quantity, BigDecimal salePrice);

    Order removeItemFromOrder(UUID orderId, UUID productId);

    Order updateItemQuantityInOrder(UUID orderId, UUID productId, int newQuantity);

    Order finalizeOrder(UUID orderId);

    Order processPayment(UUID orderId); 

    Order deliverOrder(UUID orderId);

    Order cancelOrder(UUID orderId);
}
