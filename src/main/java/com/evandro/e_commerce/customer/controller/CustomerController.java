package com.evandro.e_commerce.customer.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.service.CustomerService;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> createCustomer(@RequestBody CustomerCreationRequest request) {
        try {
            CustomerResponse customer = customerService.createCustomer(request);
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (InvalidCustomerDataException e) {
            return ResponseEntity.badRequest().build(); 
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getCustomerById(@PathVariable UUID id) {
        return customerService.findCustomerById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<CustomerResponse>> getAllCustomers() {
        List<CustomerResponse> customers = customerService.listAllCustomer();
        return ResponseEntity.ok(customers);
    }

    @GetMapping("/active")
    public ResponseEntity<List<CustomerResponse>> getActiveCustomers() {
        List<CustomerResponse> customers = customerService.listActiveCustomer();
        return ResponseEntity.ok(customers);
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerResponse> updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        try {
            CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
            return ResponseEntity.ok(updatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (InvalidCustomerDataException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<CustomerResponse> deactivateCustomer(@PathVariable UUID id) {
        try {
            CustomerResponse deactivatedCustomer = customerService.deactivateCustomer(id);
            return ResponseEntity.ok(deactivatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<CustomerResponse> activateCustomer(@PathVariable UUID id) {
        try {
            CustomerResponse activatedCustomer = customerService.activateCustomer(id);
            return ResponseEntity.ok(activatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}