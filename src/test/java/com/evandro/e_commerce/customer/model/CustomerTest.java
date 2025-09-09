package com.evandro.e_commerce.customer.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

public class CustomerTest {


    @Test
    @DisplayName("Should create customer with valid documents, address and register info")
    void shouldCreateCustomerWithValidData() {

        CustomerDocuments documents = new CustomerDocuments("John Doe",
                LocalDate.of(1990, 12, 5),
                "055.988.988-77", "10.444.234-2");

        CustomerAddress address = new CustomerAddress("85300-200",
                "rua dos canarios", 44);

        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);


        Customer customer = new Customer(documents, address, registerInfo);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals(documents, customer.getDocuments());
        assertEquals(address, customer.getAddress());
        assertEquals(registerInfo, customer.getRegisterInfo());
        assertTrue(customer.getHistory().isEmpty());
        assertTrue(customer.getPaymentMethods().isEmpty());
    }
}
