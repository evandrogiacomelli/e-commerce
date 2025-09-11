package com.evandro.e_commerce.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.evandro.e_commerce.order.exception.OrderNotFoundException;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.repository.OrderRepository;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.service.ProductService;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final ProductService productService; 
    // private final CustomerRepository customerRepository; 

    public OrderServiceImpl(OrderRepository orderRepository, ProductService productService/* , CustomerRepository customerRepository*/) {
        this.orderRepository = orderRepository;
        this.productService = productService;
        // this.customerRepository = customerRepository;
    }

    // @Override
    // public Order createOrder(UUID customerId) {
    //     Customer customer = customerRepository.findById(customerId)
    //             .orElseThrow(() -> new CustomerNotFoundException("Customer with ID " + customerId + " not found."));
    //     Order order = new Order(customer);
    //     return orderRepository.save(order);
    // }

    @Override
    public Optional<Order> findOrderById(UUID orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> listAllOrders() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> listOrdersByCustomerId(UUID customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public Order addItemToOrder(UUID orderId, UUID productId, int quantity, BigDecimal salePrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        Product product = productService.findProductById(productId)
                .orElseThrow(() -> new ProductNotFoundException("Product with ID " + productId + " not found."));

        order.addItem(product, quantity, salePrice);
        return orderRepository.save(order);
    }

    @Override
    public Order removeItemFromOrder(UUID orderId, UUID productId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.removeItem(productId);
        return orderRepository.save(order);
    }

    @Override
    public Order updateItemQuantityInOrder(UUID orderId, UUID productId, int newQuantity) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.updateItemQuantity(productId, newQuantity);
        return orderRepository.save(order);
    }

    @Override
    public Order finalizeOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.finalizeOrder();
        return orderRepository.save(order);
    }

    @Override
    public Order processPayment(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.payOrder();
        return orderRepository.save(order);
    }

    @Override
    public Order deliverOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.deliverOrder();
        return orderRepository.save(order);
    }

    @Override
    public Order cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException("Order with ID " + orderId + " not found."));

        order.cancelOrder();
        return orderRepository.save(order);
    }

}
