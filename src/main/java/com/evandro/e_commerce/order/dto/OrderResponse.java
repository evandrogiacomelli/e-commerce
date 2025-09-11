package com.evandro.e_commerce.order.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.model.OrderStatus;
import com.evandro.e_commerce.order.model.PaymentStatus;

public class OrderResponse {

    private final UUID id;
    // private CustomerResponse customer; 
    private final LocalDateTime createdAt;
    private final OrderStatus status;
    private final PaymentStatus paymentStatus;
    private final List<OrderItemResponse> items;
    private final BigDecimal totalValue;

    public OrderResponse(Order order) {
        this.id = order.getId();
        // this.customer = new CustomerResponse(order.getCustomer());
        this.createdAt = order.getCreatedAt();
        this.status = order.getStatus();
        this.paymentStatus = order.getPaymentStatus();
        this.items = order.getItems().stream()
                .map(OrderItemResponse::new)
                .collect(Collectors.toList());
        this.totalValue = order.getTotalValue();
    }

    public UUID getId() {
        return id;
    }

    // public CustomerResponse getCustomer() {
    //     return customer;
    // }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public List<OrderItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

}
