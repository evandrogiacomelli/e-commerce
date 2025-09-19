package com.evandro.e_commerce.customer.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.InvalidAddressException;
import com.evandro.e_commerce.customer.exception.InvalidCpfException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.exception.InvalidRgException;
import com.evandro.e_commerce.customer.model.CustomerAddress;
import com.evandro.e_commerce.customer.model.CustomerDocuments;
import com.evandro.e_commerce.customer.model.CustomerRegisterInfo;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.customer.repository.CustomerRepository;

@SpringBootTest
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    private CustomerRequest validRequest;
    private CustomerCreationRequest validCreationRequest;

    @BeforeEach
    void setUp() {

        customerRepository.deleteAll();

        validRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);

        CustomerDocuments validDocs = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5), "055.988.200-77", "10.444.234-2", "evandro@test.com");
        CustomerAddress validAddr = new CustomerAddress("83200-200", "rua dos canarios", 44);
        CustomerRegisterInfo activeInfo = new CustomerRegisterInfo(CustomerStatus.ACTIVE);
        validCreationRequest = new CustomerCreationRequest(validDocs, validAddr, activeInfo);
    }

    @Test
    @DisplayName("Should create and save new Customer")
    void shouldCreateAndSaveNewCustomer() {
        // Act
        CustomerResponse customer = customerService.createCustomer(validCreationRequest);

        // Assert
        assertNotNull(customer);
        assertNotNull(customer.getId());
        assertEquals("Evandro", customer.getName());
        assertEquals(CustomerStatus.ACTIVE, customer.getStatus());
    }

    @Test
    @DisplayName("Should throw InvalidCustomerDataException when name is null in documents")
    void shouldThrowExceptionWhenNameIsNullInDocuments() {
        // Arrange
        CustomerDocuments invalidDocs = new CustomerDocuments(null, LocalDate.of(1994, 10, 5), "055.988.200-77", "10.444.234-2");
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(invalidDocs, validCreationRequest.address(), validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "Name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw InvalidCustomerDataException when name is empty in documents")
    void shouldThrowExceptionWhenNameIsEmptyInDocuments() {
        // Arrange
        CustomerDocuments invalidDocs = new CustomerDocuments("", LocalDate.of(1994, 10, 5), "055.988.200-77", "10.444.234-2");
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(invalidDocs, validCreationRequest.address(), validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidCustomerDataException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "Name cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw InvalidCpfException when CPF is invalid in documents")
    void shouldThrowExceptionWhenCpfIsInvalidInDocuments() {
        // Arrange
        CustomerDocuments invalidCpfDocs = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5), "999", "10.444.234-2");
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(invalidCpfDocs, validCreationRequest.address(), validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidCpfException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "invalid cpf: 999");
    }

    @Test
    @DisplayName("Should throw InvalidRgException when RG is null in documents")
    void shouldThrowExceptionWhenRgIsNullInDocuments() {
        // Arrange
        CustomerDocuments invalidRgDocs = new CustomerDocuments("Evandro", LocalDate.of(1994, 10, 5), "055.988.200-77", null);
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(invalidRgDocs, validCreationRequest.address(), validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidRgException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "RG cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw InvalidAddressException when ZIP code is null in address")
    void shouldThrowExceptionWhenZipCodeIsNullInAddress() {
        // Arrange
        CustomerAddress invalidAddr = new CustomerAddress(null, "rua dos canarios", 44);
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(validCreationRequest.documents(), invalidAddr, validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidAddressException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "ZIP code cannot be null or empty");
    }

    @Test
    @DisplayName("Should throw InvalidAddressException when Street is null in address")
    void shouldThrowExceptionWhenStreetIsNullInAddress() {
        // Arrange
        CustomerAddress invalidAddr = new CustomerAddress("83200-200", null, 44);
        CustomerCreationRequest invalidRequest = new CustomerCreationRequest(validCreationRequest.documents(), invalidAddr, validCreationRequest.registerInfo());

        // Act & Assert
        assertThrows(InvalidAddressException.class, () -> {
            customerService.createCustomer(invalidRequest);
        }, "Street cannot be null or empty");
    }

    @Test
    @DisplayName("Should return Customer by ID.")
    void shouldFindCustomerById() {
        CustomerResponse customerSaved = customerService.createCustomer(validCreationRequest); // Use validCreationRequest
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
        customerService.createCustomer(validCreationRequest);

        // Create second customer with different CPF and RG and email
        CustomerDocuments docs2 = new CustomerDocuments("João", LocalDate.of(1985, 5, 10), "111.222.333-44", "22.333.444-5", "joao@test.com");
        CustomerCreationRequest request2 = new CustomerCreationRequest(docs2, validCreationRequest.address(), validCreationRequest.registerInfo());
        customerService.createCustomer(request2);

        // Create third customer with different CPF and RG and email
        CustomerDocuments docs3 = new CustomerDocuments("Maria", LocalDate.of(1990, 8, 15), "222.333.444-55", "33.444.555-6", "maria@test.com");
        CustomerCreationRequest request3 = new CustomerCreationRequest(docs3, validCreationRequest.address(), validCreationRequest.registerInfo());
        customerService.createCustomer(request3);

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
        CustomerResponse customer1 = customerService.createCustomer(validCreationRequest);

        // Create second customer with different CPF and RG and email
        CustomerDocuments docs2 = new CustomerDocuments("Pedro", LocalDate.of(1988, 3, 20), "333.444.555-66", "44.555.666-7", "pedro@test.com");
        CustomerCreationRequest request2 = new CustomerCreationRequest(docs2, validCreationRequest.address(), validCreationRequest.registerInfo());
        CustomerResponse customer2 = customerService.createCustomer(request2);

        // Create third customer with different CPF and RG and email
        CustomerDocuments docs3 = new CustomerDocuments("Ana", LocalDate.of(1992, 7, 12), "444.555.666-77", "55.666.777-8", "ana@test.com");
        CustomerCreationRequest request3 = new CustomerCreationRequest(docs3, validCreationRequest.address(), validCreationRequest.registerInfo());
        CustomerResponse customer3 = customerService.createCustomer(request3);

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
        CustomerResponse customer = customerService.createCustomer(validCreationRequest);
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
        CustomerResponse customer = customerService.createCustomer(validCreationRequest);
        Optional<CustomerResponse> customerBydId = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.ACTIVE, customerBydId.get().getStatus());
        customerService.deactivateCustomer(customer.getId());
        Optional<CustomerResponse> customerBydIdUpdated = customerService.findCustomerById(customer.getId());
        assertEquals(CustomerStatus.INACTIVE, customerBydIdUpdated.get().getStatus());
    }

    @Test
    @DisplayName("Should activate Consumer")
    void shouldActivateAndReturnCustomer() {
        CustomerResponse customer = customerService.createCustomer(validCreationRequest);
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
