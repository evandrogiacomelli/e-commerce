package com.evandro.e_commerce.customer.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.evandro.e_commerce.customer.model.Customer;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;

public class InMemoryCustomerRepositoryTest {

    private InMemoryCustomerRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCustomerRepository();
    }

    @Test
    @DisplayName("Should save a new customer")
    void shouldSaveNewCustomer() {
        CustomerDocuments documents = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2");
        CustomerAddress address = new CustomerAddress("83200-200", "rua dos canarios", 44);
        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        Customer customer = new Customer(documents, address, registerInfo);

        Customer savedCustomer = repository.save(customer);

        assertNotNull(savedCustomer);
        assertEquals(customer.getId(), savedCustomer.getId());
        assertEquals(customer.getDocuments().getName(), savedCustomer.getDocuments().getName());
        assertTrue(repository.findById(customer.getId()).isPresent());
    }

    @Test
    @DisplayName("Should find customer by ID")
    void shouldFindCustomerById() {
        CustomerDocuments documents = new CustomerDocuments("Mtz", LocalDate.of(1990, 3, 25),
                "200.876.234-22", "10.200.345-7");
        CustomerAddress address = new CustomerAddress("83200-200", "rua dos canarios", 44);
        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        Customer customer = new Customer(documents, address, registerInfo);
        repository.save(customer);

        Optional<Customer> foundCustomer = repository.findById(customer.getId());

        assertTrue(foundCustomer.isPresent());
        assertEquals(customer.getDocuments().getName(), foundCustomer.get().getDocuments().getName());
    }

    @Test
    @DisplayName("Should return empty optional if customer not found by ID")
    void shouldReturnEmptyOptionalWhenCustomerNotFound() {
        Optional<Customer> foundCustomer = repository.findById(UUID.randomUUID());

        assertFalse(foundCustomer.isPresent());
    }

    @Test
    @DisplayName("Should return all customers")
    void shouldReturnAllCustomers() {
        CustomerDocuments documents1 = new CustomerDocuments("João", LocalDate.of(1985, 5, 15),
                "111.222.333-44", "11.222.333-4");
        CustomerAddress address1 = new CustomerAddress("12345-678", "rua das flores", 10);
        CustomerRegisterInfo registerInfo1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        Customer customer1 = new Customer(documents1, address1, registerInfo1);

        CustomerDocuments documents2 = new CustomerDocuments("Maria", LocalDate.of(1992, 8, 20),
                "555.666.777-88", "55.666.777-8");
        CustomerAddress address2 = new CustomerAddress("98765-432", "avenida central", 25);
        CustomerRegisterInfo registerInfo2 = new CustomerRegisterInfo(CustomerStatus.INACTIVE);
        Customer customer2 = new Customer(documents2, address2, registerInfo2);

        repository.save(customer1);
        repository.save(customer2);

        List<Customer> allCustomers = repository.findAll();

        assertNotNull(allCustomers);
        assertEquals(2, allCustomers.size());
        assertTrue(allCustomers.contains(customer1));
        assertTrue(allCustomers.contains(customer2));
    }

    @Test
    @DisplayName("Should return only active customers")
    void shouldReturnOnlyActiveCustomers() {
        CustomerDocuments documents1 = new CustomerDocuments("Pedro", LocalDate.of(1988, 12, 10),
                "999.888.777-66", "99.888.777-6");
        CustomerAddress address1 = new CustomerAddress("11111-222", "rua verde", 5);
        CustomerRegisterInfo registerInfo1 = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        Customer activeCustomer = new Customer(documents1, address1, registerInfo1);

        CustomerDocuments documents2 = new CustomerDocuments("Ana", LocalDate.of(1995, 2, 28),
                "444.333.222-11", "44.333.222-1");
        CustomerAddress address2 = new CustomerAddress("33333-444", "rua azul", 15);
        CustomerRegisterInfo registerInfo2 = new CustomerRegisterInfo(CustomerStatus.INACTIVE);
        Customer inactiveCustomer = new Customer(documents2, address2, registerInfo2);

        repository.save(activeCustomer);
        repository.save(inactiveCustomer);

        List<Customer> activeCustomers = repository.findActiveCustomers();

        assertNotNull(activeCustomers);
        assertEquals(1, activeCustomers.size());
        assertTrue(activeCustomers.contains(activeCustomer));
        assertFalse(activeCustomers.contains(inactiveCustomer));
        assertEquals(CustomerStatus.ACTIVE, activeCustomers.get(0).getRegisterInfo().getStatus());
    }

    @Test
    @DisplayName("Should update an existing customer")
    void shouldUpdateExistingCustomer() {
        CustomerDocuments documents = new CustomerDocuments("Carlos", LocalDate.of(1980, 7, 3),
                "777.666.555-44", "77.666.555-4");
        CustomerAddress address = new CustomerAddress("55555-666", "rua antiga", 30);
        CustomerRegisterInfo registerInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        Customer customer = new Customer(documents, address, registerInfo);
        repository.save(customer);

        CustomerDocuments newDocuments = new CustomerDocuments("Carlos Silva", LocalDate.of(1980, 7, 3),
                "777.666.555-44", "77.666.555-4");
        CustomerAddress newAddress = new CustomerAddress("77777-888", "rua nova", 35);
        CustomerRegisterInfo newRegisterInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        customer.update(newDocuments, newAddress, newRegisterInfo);
        repository.save(customer);

        Optional<Customer> updatedCustomer = repository.findById(customer.getId());
        assertTrue(updatedCustomer.isPresent());
        assertEquals("Carlos Silva", updatedCustomer.get().getDocuments().getName());
        assertEquals("77777-888", updatedCustomer.get().getAddress().getZipCode());
    }
}