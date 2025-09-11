package com.evandro.e_commerce.customer.service;

import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.model.*;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import com.evandro.e_commerce.customer.repository.InMemoryCustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CustomerServiceTest {

    private CustomerService customerService;
    private CustomerRequest validRequest;

    @BeforeEach
    void setUp() {
        CustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerService = new CustomerServiceImpl(customerRepository);

        validRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);
    }

    @Test
    @DisplayName("Should create and save new Customer")
    void shouldCreateAndSaveNewCustomer() {
        CustomerResponse customer = customerService.createCustomer(validRequest);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Evandro", customer.getName());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
    }

    @Test
    @DisplayName("Should throw Exception with null Documents")
    void shouldThrowExceptionWhenCreatingWithNullDocuments() {
        CustomerRequest invalidRequest = new CustomerRequest(null, LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.createCustomer(invalidRequest);
        });
    }

    @Test
    @DisplayName("Should throw Exception with null Address")
    void shouldThrowExceptionWhenCreatingWithNullAddress() {
        CustomerRequest invalidRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", null, "rua dos canarios", 44);
        assertThrows(InvalidAddressException.class, () -> {
            customerService.createCustomer(invalidRequest);
        });
    }

    @Test
    @DisplayName("Should throw Exception with invalid CPF")
    void shouldThrowExceptionWhenCreatingWithInvalidCpf() {
        CustomerRequest invalidRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "999", "10.444.234-2", "83200-200", "rua dos canarios", 44);
        assertThrows(InvalidCpfException.class, () -> {
            customerService.createCustomer(invalidRequest);
        });
    }


    @Test
    @DisplayName("Should return Customer by ID.")
    void shouldFindCustomerById() {
        CustomerResponse customerSaved = customerService.createCustomer(validRequest);
        Optional<CustomerResponse> result = customerService.findCustomerById(customerSaved.getId());

        assertTrue(result.isPresent());
        assertEquals(customerSaved.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty Optional When Costumer Not Found")
    void shouldReturnEmptyOptionalWhenCostumerNotFound() {
        UUID id = UUID.randomUUID();
        Optional<CustomerResponse> result = customerService.findCustomerById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return a list with all customers.")
    void shouldListAllCustomers() {
        customerService.createCustomer(validRequest);
        customerService.createCustomer(validRequest);
        customerService.createCustomer(validRequest);

        List<CustomerResponse> customerList = customerService.listAllCustomer();

        assertNotNull(customerList);
        assertFalse(customerList.isEmpty());
        assertEquals(3, customerList.size());
    }

    @Test
    @DisplayName("Should return a Empty list of customers.")
    void shouldReturnEmptyListOfCustomers() {
        List<CustomerResponse> customerList = customerService.listAllCustomer();

        assertNotNull(customerList);
        assertTrue(customerList.isEmpty());
        assertEquals(0, customerList.size());
    }

    @Test
    @DisplayName("Should return a list of active Customers.")
    void shouldReturnListOfActiveCustomers() {
        CustomerResponse customer1 = customerService.createCustomer(validRequest);
        CustomerResponse customer2 = customerService.createCustomer(validRequest);
        CustomerResponse customer3 = customerService.createCustomer(validRequest);
        
        customerService.deactivateCustomer(customer1.getId());

        List<CustomerResponse> listOfActives = customerService.listActiveCustomer();

        assertNotNull(listOfActives);
        assertEquals(2, listOfActives.size());
        assertFalse(listOfActives.isEmpty());

        for (CustomerResponse customer : listOfActives) {
            assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
            assertNotEquals(CustomerStatus.INACTIVE, customer.getStatus());
        }
    }

    @Test
    @DisplayName("Should update and return Customer")
    void shouldUpdateAndReturnCustomer() {
        CustomerResponse customer = customerService.createCustomer(validRequest);
        CustomerRequest updateRequest = new CustomerRequest("Mtz", LocalDate.of(1990, 3, 25), 
                "200.876.234-22", "10.200.345-7", "83200-200", "rua dos canarios", 44);

        Optional<CustomerResponse> customerBydId = customerService.findCustomerById(customer.getId());

        assertEquals("Evandro", customerBydId.get().getName());

        customerService.updateCustomer(customer.getId(), updateRequest);

        Optional<CustomerResponse> customerBydIdUpdated = customerService.findCustomerById(customer.getId());

        assertEquals("Mtz", customerBydIdUpdated.get().getName());
    }

    @Test
    @DisplayName("Should deactivate Consumer")
    void shouldDeactivateAndReturnCustomer() {
        CustomerResponse customer = customerService.createCustomer(validRequest);

        Optional<CustomerResponse> customerBydId = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.ACTIVE, customerBydId.get().getStatus());

        customerService.deactivateCustomer(customer.getId());
        Optional<CustomerResponse> customerBydIdUpdated = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.INACTIVE, customerBydIdUpdated.get().getStatus());
    }

    @Test
    @DisplayName("Should activate Consumer")
    void shouldActivateAndReturnCustomer() {
        CustomerResponse customer = customerService.createCustomer(validRequest);

        Optional<CustomerResponse> customerBydId = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.ACTIVE, customerBydId.get().getStatus());

        customerService.deactivateCustomer(customer.getId());
        Optional<CustomerResponse> customerBydIdUpdatedInactive = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.INACTIVE, customerBydIdUpdatedInactive.get().getStatus());

        customerService.activateCustomer(customer.getId());
        Optional<CustomerResponse> customerBydIdUpdated = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.ACTIVE, customerBydIdUpdated.get().getStatus());
    }
}
