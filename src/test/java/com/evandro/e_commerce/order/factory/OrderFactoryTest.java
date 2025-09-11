package com.evandro.e_commerce.order.factory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus; 
import com.evandro.e_commerce.order.exception.InvalidOrderDataException;
import com.evandro.e_commerce.order.model.Order;
import com.evandro.e_commerce.order.model.OrderStatus;
import com.evandro.e_commerce.order.model.PaymentStatus;

public class OrderFactoryTest {

    private Customer testCustomer;
    private Customer inactiveCustomer; 

    @BeforeEach
    void setUp() {
        CustomerDocuments documents = new CustomerDocuments("Test Customer", LocalDate.of(1990, 1, 1), "111.222.333-44", "1234567");
        CustomerAddress address = new CustomerAddress("12345-678", "Test Street", 100);
        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        testCustomer = new Customer(documents, address, registerInfo);

        CustomerRegisterInfo inactiveInfo = new CustomerRegisterInfo(CustomerStatus.INACTIVE);
        inactiveCustomer = new Customer(documents, address, inactiveInfo);
    }

    @Test
    @DisplayName("Should create an order with valid customer")
    void shouldCreateOrderWithValidCustomer() {
        // Act
        Order order = OrderFactory.create(testCustomer);

        // Assert
        assertNotNull(order);
        assertEquals(testCustomer, order.getCustomer());
        assertEquals(OrderStatus.OPEN, order.getStatus());
        assertEquals(PaymentStatus.PENDING, order.getPaymentStatus());
        assertTrue(order.getItems().isEmpty());
    }

    @Test
    @DisplayName("Should throw InvalidOrderDataException when customer is null")
    void shouldThrowExceptionWhenCustomerIsNull() {
        // Act & Assert
        assertThrows(InvalidOrderDataException.class, () -> OrderFactory.create(null),
                "Customer cannot be null when creating an order.");
    }

    @Test 
    @DisplayName("Should throw InvalidOrderDataException when customer is inactive")
    void shouldThrowExceptionWhenCustomerIsInactive() {
        // Act & Assert
        assertThrows(InvalidOrderDataException.class, () -> OrderFactory.create(inactiveCustomer),
                "Order cannot be created for an inactive customer.");
    }
}
