package com.evandro.e_commerce.customer.controller;

import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.customer.service.CustomerService;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CustomerService customerService;

    private CustomerRequest validRequest;
    private CustomerResponse validResponse;
    private UUID customerId;

    @BeforeEach
    void setUp() {
        validRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);
        
        customerId = UUID.randomUUID();
        validResponse = createMockCustomerResponse(customerId, "Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44, CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Should create a new customer and return 201 CREATED")
    void shouldCreateCustomer() throws Exception {
        when(customerService.createCustomer(any(CustomerCreationRequest.class))).thenReturn(validResponse);
        
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Evandro"))
                .andExpect(jsonPath("$.cpf").value("055.988.200-77"))
                .andExpect(jsonPath("$.status").value(CustomerStatus.ACTIVE.toString()));
    }

    @Test
    @DisplayName("Should return 400 BAD REQUEST when creating customer with invalid data")
    void shouldReturnBadRequestWhenCreatingCustomerWithInvalidData() throws Exception {
        when(customerService.createCustomer(any(CustomerCreationRequest.class)))
                .thenThrow(new InvalidCustomerDataException("Invalid data"));

        CustomerRequest invalidRequest = new CustomerRequest(null, LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get customer by ID and return 200 OK")
    void shouldGetCustomerById() throws Exception {
        when(customerService.findCustomerById(customerId)).thenReturn(Optional.of(validResponse));

        mockMvc.perform(get("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerId.toString()))
                .andExpect(jsonPath("$.name").value("Evandro"));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when customer ID does not exist")
    void shouldReturnNotFoundWhenCustomerIdDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(customerService.findCustomerById(nonExistentId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all customers and return 200 OK")
    void shouldGetAllCustomers() throws Exception {
        CustomerResponse customer2 = createMockCustomerResponse(UUID.randomUUID(), "Maria", 
                LocalDate.of(1990, 1, 1), "222.333.444-55", "22.333.444-5", 
                "54321-987", "Another Street", 123, CustomerStatus.ACTIVE);
                
        when(customerService.listAllCustomer()).thenReturn(Arrays.asList(validResponse, customer2));

        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Evandro"))
                .andExpect(jsonPath("$[1].name").value("Maria"));
    }

    @Test
    @DisplayName("Should get active customers and return 200 OK")
    void shouldGetActiveCustomers() throws Exception {
        when(customerService.listActiveCustomer()).thenReturn(Arrays.asList(validResponse));

        mockMvc.perform(get("/customers/active")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Evandro"));
    }

    @Test
    @DisplayName("Should update an existing customer and return 200 OK")
    void shouldUpdateCustomer() throws Exception {
        CustomerRequest updateRequest = new CustomerRequest("Mtz", LocalDate.of(1990, 3, 25),
                "200.876.234-22", "10.200.345-7", "83200-200", "rua dos canarios", 44);
                
        CustomerResponse updatedResponse = createMockCustomerResponse(customerId, "Mtz", 
                LocalDate.of(1990, 3, 25), "200.876.234-22", "10.200.345-7", 
                "83200-200", "rua dos canarios", 44, CustomerStatus.ACTIVE);

        when(customerService.updateCustomer(eq(customerId), any(CustomerRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/customers/{id}", customerId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Mtz"))
                .andExpect(jsonPath("$.cpf").value("200.876.234-22"));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when updating non-existent customer")
    void shouldReturnNotFoundWhenUpdatingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(customerService.updateCustomer(eq(nonExistentId), any(CustomerRequest.class)))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(put("/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should deactivate a customer and return 200 OK")
    void shouldDeactivateCustomer() throws Exception {
        CustomerResponse deactivatedResponse = createMockCustomerResponse(customerId, "Evandro", 
                LocalDate.of(1994, 10, 5), "055.988.200-77", "10.444.234-2", 
                "83200-200", "rua dos canarios", 44, CustomerStatus.INACTIVE);

        when(customerService.deactivateCustomer(customerId)).thenReturn(deactivatedResponse);

        mockMvc.perform(patch("/customers/{id}/deactivate", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CustomerStatus.INACTIVE.toString()));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deactivating non-existent customer")
    void shouldReturnNotFoundWhenDeactivatingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(customerService.deactivateCustomer(nonExistentId))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(patch("/customers/{id}/deactivate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should activate a customer and return 200 OK")
    void shouldActivateCustomer() throws Exception {
        CustomerResponse activatedResponse = createMockCustomerResponse(customerId, "Evandro", 
                LocalDate.of(1994, 10, 5), "055.988.200-77", "10.444.234-2", 
                "83200-200", "rua dos canarios", 44, CustomerStatus.ACTIVE);

        when(customerService.activateCustomer(customerId)).thenReturn(activatedResponse);

        mockMvc.perform(patch("/customers/{id}/activate", customerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CustomerStatus.ACTIVE.toString()));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when activating non-existent customer")
    void shouldReturnNotFoundWhenActivatingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();
        when(customerService.activateCustomer(nonExistentId))
                .thenThrow(new CustomerNotFoundException("Customer not found"));

        mockMvc.perform(patch("/customers/{id}/activate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
    
    private CustomerResponse createMockCustomerResponse(UUID id, String name, LocalDate birthDate,
                                                       String cpf, String rg, String zipCode, 
                                                       String street, int number, CustomerStatus status) {
        CustomerResponse response = new CustomerResponse();
        try {
            setField(response, "id", id);
            setField(response, "name", name);
            setField(response, "birthDate", birthDate);
            setField(response, "cpf", cpf);
            setField(response, "rg", rg);
            setField(response, "zipCode", zipCode);
            setField(response, "street", street);
            setField(response, "number", number);
            setField(response, "registerDate", LocalDateTime.now());
            setField(response, "lastAccess", LocalDateTime.now());
            setField(response, "status", status);
            setField(response, "inactiveIn", null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create mock customer response", e);
        }
        return response;
    }
    
    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}