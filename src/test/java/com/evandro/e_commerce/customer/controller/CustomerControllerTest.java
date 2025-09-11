package com.evandro.e_commerce.customer.controller;

import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.model.CustomerStatus;
import com.evandro.e_commerce.customer.repository.CustomerRepository;
import com.evandro.e_commerce.customer.repository.InMemoryCustomerRepository;
import com.evandro.e_commerce.customer.service.CustomerService;
import com.evandro.e_commerce.customer.service.CustomerServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
public class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerService customerService;

    private CustomerRequest validRequest;

    private static InMemoryCustomerRepository customerRepository = new InMemoryCustomerRepository();

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CustomerService customerService() {
            return new CustomerServiceImpl(customerRepository);
        }
    }

    @BeforeEach
    void setUp() {
        customerRepository.clear();
        validRequest = new CustomerRequest("Evandro", LocalDate.of(1994, 10, 5),
                "055.988.200-77", "10.444.234-2", "83200-200", "rua dos canarios", 44);
    }

    @Test
    @DisplayName("Should create a new customer and return 201 CREATED")
    void shouldCreateCustomer() throws Exception {
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
        String createResponse = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CustomerResponse createdCustomer = objectMapper.readValue(createResponse, CustomerResponse.class);

        mockMvc.perform(get("/customers/{id}", createdCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(createdCustomer.getId().toString()))
                .andExpect(jsonPath("$.name").value("Evandro"));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when customer ID does not exist")
    void shouldReturnNotFoundWhenCustomerIdDoesNotExist() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all customers and return 200 OK")
    void shouldGetAllCustomers() throws Exception {
        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/customers")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Evandro"));
    }

    @Test
    @DisplayName("Should get active customers and return 200 OK")
    void shouldGetActiveCustomers() throws Exception {
        String createResponse = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CustomerResponse createdCustomer = objectMapper.readValue(createResponse, CustomerResponse.class);

        mockMvc.perform(get("/customers/active")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));

        mockMvc.perform(patch("/customers/{id}/deactivate", createdCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Should update an existing customer and return 200 OK")
    void shouldUpdateCustomer() throws Exception {
        String createResponse = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CustomerResponse createdCustomer = objectMapper.readValue(createResponse, CustomerResponse.class);

        CustomerRequest updateRequest = new CustomerRequest("Mtz", LocalDate.of(1990, 3, 25),
                "200.876.234-22", "10.200.345-7", "83200-200", "rua dos canarios", 44);

        mockMvc.perform(put("/customers/{id}", createdCustomer.getId())
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

        mockMvc.perform(put("/customers/{id}", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should deactivate a customer and return 200 OK")
    void shouldDeactivateCustomer() throws Exception {
        String createResponse = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CustomerResponse createdCustomer = objectMapper.readValue(createResponse, CustomerResponse.class);

        mockMvc.perform(patch("/customers/{id}/deactivate", createdCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CustomerStatus.INACTIVE.toString()));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when deactivating non-existent customer")
    void shouldReturnNotFoundWhenDeactivatingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(patch("/customers/{id}/deactivate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should activate a customer and return 200 OK")
    void shouldActivateCustomer() throws Exception {
        String createResponse = mockMvc.perform(post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        CustomerResponse createdCustomer = objectMapper.readValue(createResponse, CustomerResponse.class);

        mockMvc.perform(patch("/customers/{id}/deactivate", createdCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/customers/{id}/activate", createdCustomer.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(CustomerStatus.ACTIVE.toString()));
    }

    @Test
    @DisplayName("Should return 404 NOT FOUND when activating non-existent customer")
    void shouldReturnNotFoundWhenActivatingNonExistentCustomer() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(patch("/customers/{id}/activate", nonExistentId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}