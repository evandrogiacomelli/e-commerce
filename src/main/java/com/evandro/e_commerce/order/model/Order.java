package com.evandro.e_commerce.order.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.*;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.product.model.Product;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(columnDefinition = "UUID")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false)
    private PaymentStatus paymentStatus;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> items;

    public Order() {
        this.items = new ArrayList<>();
    }

    public Order(Customer customer) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null for an order.");
        }
        this.customer = customer;
        this.createdAt = LocalDateTime.now();
        this.status = OrderStatus.OPEN;
        this.paymentStatus = PaymentStatus.PENDING;
        this.items = new ArrayList<>();
    }

    public UUID getId() {
        return id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    public void addItem(Product product, int quantity, BigDecimal salePrice) {
        if (this.status != OrderStatus.OPEN) {
            throw new IllegalStateException("Cannot add items to an order that is not OPEN.");
        }
        Optional<OrderItem> existingItem = items.stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().updateQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            OrderItem newItem = new OrderItem(product, quantity, salePrice);
            newItem.setOrder(this);
            items.add(newItem);
        }
    }

    public void removeItem(UUID productId) {
        if (this.status != OrderStatus.OPEN) {
            throw new IllegalStateException("Cannot remove items from an order that is not OPEN.");
        }
        boolean removed = items.removeIf(item -> item.getProduct().getId().equals(productId));
        if (!removed) {
            throw new IllegalArgumentException("Product with ID " + productId + " not found in order.");
        }
    }

    public void updateItemQuantity(UUID productId, int newQuantity) {
        if (this.status != OrderStatus.OPEN) {
            throw new IllegalStateException("Cannot update item quantity in an order that is not OPEN.");
        }
        if (newQuantity <= 0) {
            removeItem(productId);
            return;
        }

        OrderItem itemToUpdate = items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Product with ID " + productId + " not found in order."));

        itemToUpdate.updateQuantity(newQuantity);
    }

    public BigDecimal getTotalValue() {
        return items.stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void finalizeOrder() {
        if (this.status != OrderStatus.OPEN) {
            throw new IllegalStateException("Only OPEN orders can be finalized.");
        }
        if (items.isEmpty()) {
            throw new IllegalStateException("Order must have at least one item to be finalized.");
        }
        if (getTotalValue().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalStateException("Order total value must be greater than zero to be finalized.");
        }
        this.status = OrderStatus.WAITING_PAYMENT;
    }

    public void payOrder() {
        if (this.status != OrderStatus.WAITING_PAYMENT) {
            throw new IllegalStateException("Only orders with status WAITING_PAYMENT can be paid.");
        }
        this.paymentStatus = PaymentStatus.APPROVED;
        this.status = OrderStatus.PAID;
    }

    public void deliverOrder() {
        if (this.status != OrderStatus.PAID) {
            throw new IllegalStateException("Only PAID orders can be delivered.");
        }
        this.status = OrderStatus.FINISHED;
    }

    public void cancelOrder() {
        if (this.status == OrderStatus.FINISHED || this.paymentStatus == PaymentStatus.APPROVED) {
            throw new IllegalStateException("Cannot cancel an order that is already FINISHED or PAID.");
        }
        this.status = OrderStatus.CANCELLED;
        this.paymentStatus = PaymentStatus.REJECTED; 
    }
}
