package com.evandro.e_commerce.order.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.product.model.Product;

public class InMemoryOrderRepositoryTest {

    private InMemoryOrderRepository repository;
    private Customer customer1;
    private Customer customer2;
    private Product product;

    @BeforeEach
    void setUp() {
        repository = new InMemoryOrderRepository();

        // Configura clientes de teste
        CustomerDocuments doc1 = new CustomerDocuments("Customer One", LocalDate.of(1980, 1, 1), "111.111.111-11", "1234567");
        CustomerAddress addr1 = new CustomerAddress("10000-000", "Street A", 1);
        CustomerRegisterInfo info1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        customer1 = new Customer(doc1, addr1, info1);

        CustomerDocuments doc2 = new CustomerDocuments("Customer Two", LocalDate.of(1990, 2, 2), "222.222.222-22", "7654321");
        CustomerAddress addr2 = new CustomerAddress("20000-000", "Street B", 2);
        CustomerRegisterInfo info2 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        customer2 = new Customer(doc2, addr2, info2);

        product = new Product("Test Product", "Description", new BigDecimal("100.00"));
    }

    @Test
    @DisplayName("Should save a new order")
    void shouldSaveNewOrder() {
        // Arrange
        Order order = new Order(customer1);

        // Act
        Order savedOrder = repository.save(order);

        // Assert
        assertNotNull(savedOrder);
        assertEquals(order.getId(), savedOrder.getId());
        assertTrue(repository.findById(order.getId()).isPresent());
    }

    @Test
    @DisplayName("Should find order by ID")
    void shouldFindOrderById() {
        // Arrange
        Order order = new Order(customer1);
        repository.save(order);

        // Act
        Optional<Order> foundOrder = repository.findById(order.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional if order not found by ID")
    void shouldReturnEmptyOptionalWhenOrderNotFound() {
        // Act
        Optional<Order> foundOrder = repository.findById(UUID.randomUUID());

        // Assert
        assertFalse(foundOrder.isPresent());
    }

    @Test
    @DisplayName("Should return all orders")
    void shouldReturnAllOrders() {
        // Arrange
        Order order1 = new Order(customer1);
        Order order2 = new Order(customer2);
        repository.save(order1);
        repository.save(order2);

        // Act
        List<Order> allOrders = repository.findAll();

        // Assert
        assertNotNull(allOrders);
        assertEquals(2, allOrders.size());
        assertTrue(allOrders.contains(order1));
        assertTrue(allOrders.contains(order2));
    }

    @Test
    @DisplayName("Should return orders by customer ID")
    void shouldReturnOrdersByCustomerId() {
        // Arrange
        Order order1Customer1 = new Order(customer1);
        order1Customer1.addItem(product, 1, new BigDecimal("100.00"));
        Order order2Customer1 = new Order(customer1);
        order2Customer1.addItem(product, 2, new BigDecimal("100.00"));
        Order order1Customer2 = new Order(customer2);
        order1Customer2.addItem(product, 3, new BigDecimal("100.00"));

        repository.save(order1Customer1);
        repository.save(order2Customer1);
        repository.save(order1Customer2);

        // Act
        List<Order> customer1Orders = repository.findByCustomerId(customer1.getId());
        List<Order> customer2Orders = repository.findByCustomerId(customer2.getId());

        // Assert
        assertNotNull(customer1Orders);
        assertEquals(2, customer1Orders.size());
        assertTrue(customer1Orders.contains(order1Customer1));
        assertTrue(customer1Orders.contains(order2Customer1));

        assertNotNull(customer2Orders);
        assertEquals(1, customer2Orders.size());
        assertTrue(customer2Orders.contains(order1Customer2));
    }

    @Test
    @DisplayName("Should return empty list if no orders for customer ID")
    void shouldReturnEmptyListIfNoOrdersForCustomerId() {
        // Arrange
        Order order1 = new Order(customer1);
        repository.save(order1);

        // Act
        List<Order> ordersForNonExistentCustomer = repository.findByCustomerId(UUID.randomUUID());

        // Assert
        assertNotNull(ordersForNonExistentCustomer);
        assertTrue(ordersForNonExistentCustomer.isEmpty());
    }

    @Test
    @DisplayName("Should update an existing order")
    void shouldUpdateExistingOrder() {
        // Arrange
        Order order = new Order(customer1);
        repository.save(order);

        // Act
        order.addItem(product, 1, new BigDecimal("100.00"));
        order.finalizeOrder();
        repository.save(order); 

        // Assert
        Optional<Order> updatedOrder = repository.findById(order.getId());
        assertTrue(updatedOrder.isPresent());
        assertEquals(order.getStatus(), updatedOrder.get().getStatus());
        assertEquals(order.getTotalValue(), updatedOrder.get().getTotalValue());
    }
}
