package com.evandro.e_commerce.customer.service;

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
    private CustomerDocuments validDocuments;
    private CustomerAddress validAddress;
    private CustomerRegisterInfo validInfo;

    @BeforeEach
    void setUp() {
        CustomerRepository customerRepository = new InMemoryCustomerRepository();
        customerService = new CustomerServiceImpl(customerRepository);

        validDocuments = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2");
        validAddress = new CustomerAddress("83200-200", "rua dos canarios", 44);
        validInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should create and save new Customer")
    void shouldCreateAndSaveNewCustomer() {
        Customer customer = customerService.createCustomer(validDocuments, validAddress, validInfo);

        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Evandro", customer.getDocuments().getName());
        assertEquals(CustomerStatus.ACTIVE, customer.getRegisterInfo().getStatus());
    }

    @Test
    @DisplayName("Should throw Exception with null Documents")
    void shouldThrowExceptionWhenCreatingWithNullDocuments() {
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.createCustomer(null, validAddress, validInfo);
        });
    }

    @Test
    @DisplayName("Should throw Exception with null Address")
    void shouldThrowExceptionWhenCreatingWithNullAddress() {
        assertThrows(InvalidAddressException.class, () -> {
            customerService.createCustomer(validDocuments, null, validInfo);
        });
    }

    @Test
    @DisplayName("Should throw Exception with null Info")
    void shouldThrowExceptionWhenCreatingWithNullInfo() {
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.createCustomer(validDocuments, validAddress, null);
        });
    }

    @Test
    @DisplayName("Should throw Exception with invalid CPF")
    void shouldThrowExceptionWhenCreatingWithInvalidCpf() {
        assertThrows(InvalidCpfException.class, () -> {
            CustomerDocuments invalidDocuments = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5),
                    "999", "10.444.234-2");

            customerService.createCustomer(invalidDocuments, validAddress, validInfo);
        });
    }

    @Test
    @DisplayName("Should return Customer by ID.")
    void shouldFindCustomerById() {
        Customer customerSaved = customerService.createCustomer(validDocuments, validAddress, validInfo);
        Optional<Customer> result = customerService.findCustomerById(customerSaved.getId());

        assertTrue(result.isPresent());
        assertEquals(customerSaved.getId(), result.get().getId());
    }

    @Test
    @DisplayName("Should return empty Optional When Costumer Not Found")
    void shouldReturnEmptyOptionalWhenCostumerNotFound() {
        UUID id = UUID.randomUUID();
        Optional<Customer> result = customerService.findCustomerById(id);

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Should return a list with all customers.")
    void shouldListAllCustomers() {
        customerService.createCustomer(validDocuments, validAddress, validInfo);
        customerService.createCustomer(validDocuments, validAddress, validInfo);
        customerService.createCustomer(validDocuments, validAddress, validInfo);

        List<Customer> customerList = customerService.listAllCustomer();

        assertNotNull(customerList);
        assertFalse(customerList.isEmpty());
        assertEquals(3, customerList.size());
    }

    @Test
    @DisplayName("Should return a Empty list of customers.")
    void shouldReturnEmptyListOfCustomers() {
        List<Customer> customerList = customerService.listAllCustomer();

        assertNotNull(customerList);
        assertTrue(customerList.isEmpty());
        assertEquals(0, customerList.size());
    }

    @Test
    @DisplayName("Should return a list of active Customers.")
    void shouldReturnListOfActiveCustomers() {
        CustomerRegisterInfo inactiveInfo = new CustomerRegisterInfo(CustomerStatus.INACTIVE);

        customerService.createCustomer(validDocuments, validAddress, validInfo);
        customerService.createCustomer(validDocuments, validAddress, validInfo);
        customerService.createCustomer(validDocuments, validAddress, validInfo);
        customerService.createCustomer(validDocuments, validAddress, inactiveInfo);
        customerService.createCustomer(validDocuments, validAddress, inactiveInfo);
        customerService.createCustomer(validDocuments, validAddress, inactiveInfo);

        List<Customer> listOfActives = customerService.listActiveCustomer();

        assertNotNull(listOfActives);
        assertEquals(3, listOfActives.size());
        assertFalse(listOfActives.isEmpty());

        for (Customer customer : listOfActives) {
            assertEquals(CustomerStatus.ACTIVE, customer.getRegisterInfo().getStatus());
            assertNotEquals(CustomerStatus.INACTIVE, customer.getRegisterInfo().getStatus());
        }
    }




}
