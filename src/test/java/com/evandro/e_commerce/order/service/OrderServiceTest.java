package com.evandro.e_commerce.order.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;

import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import com.evandro.e_commerce.order.exception.InvalidOrderDataException;
import com.evandro.e_commerce.order.exception.OrderNotFoundException;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.model.OrderStatus;
import com.evandro.e_commerce.order.model.PaymentStatus;
import com.evandro.e_commerce.order.repository.OrderRepository;
import com.evandro.e_commerce.product.exception.ProductNotFoundException;
import com.evandro.e_commerce.product.model.Product;
import com.evandro.e_commerce.product.repository.ProductRepository;

@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    private Customer testCustomer;
    private Customer inactiveCustomer;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        customerRepository.deleteAll();
        productRepository.deleteAll();

        CustomerDocuments doc = new CustomerDocuments("Test Customer", LocalDate.of(1990, 1, 1), "111.222.333-44", "1234567", "test@email.com");
        CustomerAddress addr = new CustomerAddress("12345-678", "Test Street", 100);
        CustomerRegisterInfo info = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        testCustomer = customerRepository.save(new Customer(doc, addr, info));

        CustomerDocuments inactiveDoc = new CustomerDocuments("Inactive Customer", LocalDate.of(1990, 1, 1), "222.333.444-55", "7654321", "inactive@email.com");
        CustomerRegisterInfo inactiveInfo = new CustomerRegisterInfo(CustomerStatus.INACTIVE);
        inactiveCustomer = customerRepository.save(new Customer(inactiveDoc, addr, inactiveInfo));

        testProduct = productRepository.save(new Product("Test Product", "Description", new BigDecimal("100.00")));
    }

    @Test
    @DisplayName("Should create an order successfully")
    void shouldCreateOrderSuccessfully() {
        // Act
        Order createdOrder = orderService.createOrder(testCustomer.getId());

        // Assert
        assertNotNull(createdOrder);
        assertEquals(testCustomer.getId(), createdOrder.getCustomer().getId());
        assertEquals(OrderStatus.OPEN, createdOrder.getStatus());
        assertEquals(PaymentStatus.PENDING, createdOrder.getPaymentStatus());
        assertTrue(orderRepository.findById(createdOrder.getId()).isPresent());
    }

    @Test
    @DisplayName("Should throw CustomerNotFoundException when creating order for non-existent customer")
    void shouldThrowCustomerNotFoundExceptionWhenCreatingOrderForNonExistentCustomer() {
        // Act & Assert
        assertThrows(CustomerNotFoundException.class, () -> orderService.createOrder(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should throw InvalidOrderDataException when creating order for inactive customer")
    void shouldThrowInvalidOrderDataExceptionWhenCreatingOrderForInactiveCustomer() {
        // Act & Assert
        assertThrows(InvalidOrderDataException.class, () -> orderService.createOrder(inactiveCustomer.getId()),
                "Order cannot be created for an inactive customer.");
    }

    @Test
    @DisplayName("Should find order by ID")
    void shouldFindOrderById() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());

        // Act
        Optional<Order> foundOrder = orderService.findOrderById(order.getId());

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals(order.getId(), foundOrder.get().getId());
    }

    @Test
    @DisplayName("Should return empty optional when order not found by ID")
    void shouldReturnEmptyOptionalWhenOrderNotFoundById() {
        // Act
        Optional<Order> foundOrder = orderService.findOrderById(UUID.randomUUID());

        // Assert
        assertFalse(foundOrder.isPresent());
    }

    @Test
    @DisplayName("Should list all orders")
    void shouldListAllOrders() {
        // Arrange
        orderService.createOrder(testCustomer.getId());
        orderService.createOrder(testCustomer.getId());

        // Act
        List<Order> orders = orderService.listAllOrders();

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Should list orders by customer ID")
    void shouldListOrdersByCustomerId() {
        // Arrange
        orderService.createOrder(testCustomer.getId());
        orderService.createOrder(testCustomer.getId());

        // Act
        List<Order> orders = orderService.listOrdersByCustomerId(testCustomer.getId());

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @Test
    @DisplayName("Should add item to an OPEN order")
    void shouldAddItemToOrder() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());

        // Act
        Order updatedOrder = orderService.addItemToOrder(order.getId(), testProduct.getId(), 2, new BigDecimal("95.00"));

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        assertEquals(2, updatedOrder.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("190.00"), updatedOrder.getTotalValue());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when adding item to non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenAddingItemToNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.addItemToOrder(UUID.randomUUID(), testProduct.getId(), 1, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Should throw ProductNotFoundException when adding non-existent product to order")
    void shouldThrowProductNotFoundExceptionWhenAddingNonExistentProductToOrder() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());

        // Act & Assert
        assertThrows(ProductNotFoundException.class, () -> orderService.addItemToOrder(order.getId(), UUID.randomUUID(), 1, new BigDecimal("10.00")));
    }

    @Test
    @DisplayName("Should remove item from an OPEN order")
    void shouldRemoveItemFromOrder() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));

        // Act
        Order updatedOrder = orderService.removeItemFromOrder(order.getId(), testProduct.getId());

        // Assert
        assertNotNull(updatedOrder);
        assertTrue(updatedOrder.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, updatedOrder.getTotalValue());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when removing item from non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenRemovingItemFromNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.removeItemFromOrder(UUID.randomUUID(), testProduct.getId()));
    }

    @Test
    @DisplayName("Should update item quantity in an OPEN order")
    void shouldUpdateItemQuantityInOrder() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));

        // Act
        Order updatedOrder = orderService.updateItemQuantityInOrder(order.getId(), testProduct.getId(), 3);

        // Assert
        assertNotNull(updatedOrder);
        assertEquals(1, updatedOrder.getItems().size());
        assertEquals(3, updatedOrder.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("300.00"), updatedOrder.getTotalValue());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when updating item quantity in non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenUpdatingItemQuantityInNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.updateItemQuantityInOrder(UUID.randomUUID(), testProduct.getId(), 2));
    }

    @Test
    @DisplayName("Should finalize an order successfully")
    void shouldFinalizeOrderSuccessfully() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));

        // Act
        Order finalizedOrder = orderService.finalizeOrder(order.getId());

        // Assert
        assertNotNull(finalizedOrder);
        assertEquals(OrderStatus.WAITING_PAYMENT, finalizedOrder.getStatus());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when finalizing non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenFinalizingNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.finalizeOrder(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should process payment for an order successfully")
    void shouldProcessPaymentSuccessfully() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));
        orderService.finalizeOrder(order.getId());

        // Act
        Order paidOrder = orderService.processPayment(order.getId());

        // Assert
        assertNotNull(paidOrder);
        assertEquals(OrderStatus.PAID, paidOrder.getStatus());
        assertEquals(PaymentStatus.APPROVED, paidOrder.getPaymentStatus());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when processing payment for non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenProcessingPaymentForNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.processPayment(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should deliver an order successfully")
    void shouldDeliverOrderSuccessfully() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));
        orderService.finalizeOrder(order.getId());
        orderService.processPayment(order.getId());

        // Act
        Order deliveredOrder = orderService.deliverOrder(order.getId());

        // Assert
        assertNotNull(deliveredOrder);
        assertEquals(OrderStatus.FINISHED, deliveredOrder.getStatus());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when delivering non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenDeliveringNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.deliverOrder(UUID.randomUUID()));
    }

    @Test
    @DisplayName("Should cancel an order successfully")
    void shouldCancelOrderSuccessfully() {
        // Arrange
        Order order = orderService.createOrder(testCustomer.getId());
        orderService.addItemToOrder(order.getId(), testProduct.getId(), 1, new BigDecimal("100.00"));

        // Act
        Order cancelledOrder = orderService.cancelOrder(order.getId());

        // Assert
        assertNotNull(cancelledOrder);
        assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
        assertEquals(PaymentStatus.REJECTED, cancelledOrder.getPaymentStatus());
    }

    @Test
    @DisplayName("Should throw OrderNotFoundException when cancelling non-existent order")
    void shouldThrowOrderNotFoundExceptionWhenCancellingNonExistentOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.cancelOrder(UUID.randomUUID()));
    }
}
