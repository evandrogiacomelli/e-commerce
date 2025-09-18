package com.evandro.e_commerce.order.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.product.model.Product;

public class OrderTest {

    private Customer customer;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        CustomerDocuments documents = new CustomerDocuments("Test Customer", LocalDate.of(1990, 1, 1), "111.222.333-44", "1234567");
        CustomerAddress address = new CustomerAddress("12345-678", "Test Street", 100);
        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        customer = new Customer(documents, address, registerInfo);

        product1 = new Product("Laptop", "Powerful laptop", new BigDecimal("5000.00"));
        setProductId(product1, UUID.randomUUID());
        product2 = new Product("Mouse", "Gaming mouse", new BigDecimal("200.00"));
        setProductId(product2, UUID.randomUUID());
    }

    @Test
    @DisplayName("Should create an order with OPEN status and PENDING payment status")
    void shouldCreateOrderWithOpenAndPendingStatus() {
        // Act
        Order order = new Order(customer);

        // Assert
        assertNotNull(order);
        assertNotNull(order.getCreatedAt());
        assertEquals(customer, order.getCustomer());
        assertEquals(OrderStatus.OPEN, order.getStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertTrue(order.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, order.getTotalValue());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when creating order with null customer")
    void shouldThrowExceptionWhenCustomerIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Order(null), "Customer cannot be null for an order.");
    }

    @Test
    @DisplayName("Should add an item to an OPEN order")
    void shouldAddItemToOpenOrder() {
        // Arrange
        Order order = new Order(customer);

        // Act
        order.addItem(product1, 1, new BigDecimal("4900.00"));

        // Assert
        assertEquals(1, order.getItems().size());
        assertEquals(product1, order.getItems().get(0).getProduct());
        assertEquals(1, order.getItems().get(0).getQuantity());
        assertEquals(new BigDecimal("4900.00"), order.getTotalValue());
    }

    @Test
    @DisplayName("Should update quantity if adding an existing item to an OPEN order")
    void shouldUpdateQuantityWhenAddingExistingItem() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("4900.00")); 

        // Act
        order.addItem(product1, 2, new BigDecimal("4900.00")); 

        // Assert
        assertEquals(1, order.getItems().size());
        assertEquals(3, order.getItems().get(0).getQuantity()); 
        assertEquals(new BigDecimal("14700.00"), order.getTotalValue()); 
    }

    @Test
    @DisplayName("Should throw IllegalStateException when adding item to a non-OPEN order")
    void shouldThrowExceptionWhenAddingItemToNonOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.addItem(product2, 1, new BigDecimal("50.00")),
                "Cannot add items to an order that is not OPEN.");
    }

    @Test
    @DisplayName("Should remove an item from an OPEN order")
    void shouldRemoveItemFromOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("4900.00"));
        order.addItem(product2, 2, new BigDecimal("190.00"));

        // Act
        order.removeItem(product1.getId());

        // Assert
        assertEquals(1, order.getItems().size());
        assertEquals(product2, order.getItems().get(0).getProduct());
        assertEquals(new BigDecimal("380.00"), order.getTotalValue()); 
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when removing non-existent item")
    void shouldThrowExceptionWhenRemovingNonExistentItem() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));

        // Act & Assert
        UUID randomId = UUID.randomUUID();
        assertThrows(IllegalArgumentException.class, () -> order.removeItem(randomId),
                "Product with ID " + randomId + " not found in order.");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when removing item from a non-OPEN order")
    void shouldThrowExceptionWhenRemovingItemFromNonOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.removeItem(product1.getId()),
                "Cannot remove items from an order that is not OPEN.");
    }

    @Test
    @DisplayName("Should update item quantity in an OPEN order")
    void shouldUpdateItemQuantityInOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("4900.00"));
        order.addItem(product2, 2, new BigDecimal("190.00"));

        // Act
        order.updateItemQuantity(product1.getId(), 3);

        // Assert
        assertEquals(2, order.getItems().size());
        assertEquals(3, order.getItems().stream().filter(i -> i.getProduct().getId().equals(product1.getId())).findFirst().get().getQuantity());
        assertEquals(new BigDecimal("15080.00"), order.getTotalValue()); 
    }

    @Test
    @DisplayName("Should remove item when updating quantity to zero or less")
    void shouldRemoveItemWhenUpdatingQuantityToZero() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("4900.00"));
        order.addItem(product2, 2, new BigDecimal("190.00"));

        // Act
        order.updateItemQuantity(product1.getId(), 0);

        // Assert
        assertEquals(1, order.getItems().size());
        assertFalse(order.getItems().stream().anyMatch(i -> i.getProduct().getId().equals(product1.getId())));
        assertEquals(new BigDecimal("380.00"), order.getTotalValue());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when updating item quantity in a non-OPEN order")
    void shouldThrowExceptionWhenUpdatingItemQuantityInNonOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> order.updateItemQuantity(product1.getId(), 2),
                "Cannot update item quantity in an order that is not OPEN.");
    }

    @Test
    @DisplayName("Should finalize an order successfully")
    void shouldFinalizeOrderSuccessfully() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));

        // Act
        order.finalizeOrder();

        // Assert
        assertEquals(OrderStatus.WAITING_PAYMENT, order.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when finalizing an empty order")
    void shouldThrowExceptionWhenFinalizingEmptyOrder() {
        // Arrange
        Order order = new Order(customer);

        // Act & Assert
        assertThrows(IllegalStateException.class, order::finalizeOrder,
                "Order must have at least one item to be finalized.");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when finalizing a non-OPEN order")
    void shouldThrowExceptionWhenFinalizingNonOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, order::finalizeOrder,
                "Only OPEN orders can be finalized.");
    }

    @Test
    @DisplayName("Should pay an order successfully")
    void shouldPayOrderSuccessfully() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder();

        // Act
        order.payOrder();

        // Assert
        assertEquals(OrderStatus.PAID, order.getStatus());
        assertEquals(PaymentStatus.APPROVED, order.getPaymentStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when paying a non-WAITING_PAYMENT order")
    void shouldThrowExceptionWhenPayingNonWaitingPaymentOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));

        // Act & Assert
        assertThrows(IllegalStateException.class, order::payOrder,
                "Only orders with status WAITING_PAYMENT can be paid.");
    }

    @Test
    @DisplayName("Should deliver an order successfully")
    void shouldDeliverOrderSuccessfully() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 
        order.payOrder(); 

        // Act
        order.deliverOrder();

        // Assert
        assertEquals(OrderStatus.FINISHED, order.getStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when delivering a non-PAID order")
    void shouldThrowExceptionWhenDeliveringNonPaidOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, order::deliverOrder,
                "Only PAID orders can be delivered.");
    }

    @Test
    @DisplayName("Should cancel an OPEN order")
    void shouldCancelOpenOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));

        // Act
        order.cancelOrder();

        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(PaymentStatus.REJECTED, order.getPaymentStatus());
    }

    @Test
    @DisplayName("Should cancel a WAITING_PAYMENT order")
    void shouldCancelWaitingPaymentOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder(); 

        // Act
        order.cancelOrder();

        // Assert
        assertEquals(OrderStatus.CANCELLED, order.getStatus());
        assertEquals(PaymentStatus.REJECTED, order.getPaymentStatus());
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling a PAID order")
    void shouldThrowExceptionWhenCancellingPaidOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder();
        order.payOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, order::cancelOrder,
                "Cannot cancel an order that is already FINISHED or PAID.");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when cancelling a FINISHED order")
    void shouldThrowExceptionWhenCancellingFinishedOrder() {
        // Arrange
        Order order = new Order(customer);
        order.addItem(product1, 1, new BigDecimal("100.00"));
        order.finalizeOrder();
        order.payOrder();
        order.deliverOrder(); 

        // Act & Assert
        assertThrows(IllegalStateException.class, order::cancelOrder,
                "Cannot cancel an order that is already FINISHED or PAID.");
    }

    private void setProductId(Product product, UUID id) {
        try {
            Field field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set product ID for test", e);
        }
    }
}
