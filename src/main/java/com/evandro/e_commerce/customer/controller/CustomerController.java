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
import com.evandro.e_commerce.customer.dto.CustomerDtoConverter;
import com.evandro.e_commerce.customer.exception.CustomerNotFoundException;
import com.evandro.e_commerce.customer.exception.InvalidCustomerDataException;
import com.evandro.e_commerce.customer.service.CustomerService;
import com.evandro.e_commerce.common.dto.ErrorMessage;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping
    public ResponseEntity<Object> createCustomer(@RequestBody CustomerRequest request) {
        try {
            CustomerCreationRequest creationRequest = new CustomerCreationRequest(
                CustomerDtoConverter.toCustomerDocuments(request),
                CustomerDtoConverter.toCustomerAddress(request),
                CustomerDtoConverter.toCustomerRegisterInfo()
            );
            CustomerResponse customer = customerService.createCustomer(creationRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(customer);
        } catch (InvalidCustomerDataException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getCustomerById(@PathVariable UUID id) {
        return customerService.findCustomerById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage("Customer not found")));
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
    public ResponseEntity<Object> updateCustomer(@PathVariable UUID id, @RequestBody CustomerRequest request) {
        try {
            CustomerResponse updatedCustomer = customerService.updateCustomer(id, request);
            return ResponseEntity.ok(updatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        } catch (InvalidCustomerDataException e) {
            return ResponseEntity.badRequest().body(new ErrorMessage(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Object> deactivateCustomer(@PathVariable UUID id) {
        try {
            CustomerResponse deactivatedCustomer = customerService.deactivateCustomer(id);
            return ResponseEntity.ok(deactivatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        }
    }

    @PatchMapping("/{id}/activate")
    public ResponseEntity<Object> activateCustomer(@PathVariable UUID id) {
        try {
            CustomerResponse activatedCustomer = customerService.activateCustomer(id);
            return ResponseEntity.ok(activatedCustomer);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorMessage(e.getMessage()));
        }
    }
}