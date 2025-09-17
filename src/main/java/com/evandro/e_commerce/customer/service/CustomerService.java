package com.evandro.e_commerce.customer.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.evandro.e_commerce.customer.dto.CustomerCreationRequest;
import com.evandro.e_commerce.customer.dto.CustomerRequest;
import com.evandro.e_commerce.customer.dto.CustomerResponse;

public interface CustomerService {
    CustomerResponse createCustomer(CustomerCreationRequest request);
    Optional<CustomerResponse> findCustomerById(UUID id);
    List<CustomerResponse> listAllCustomer();
    List<CustomerResponse> listActiveCustomer();
    CustomerResponse updateCustomer(UUID id, CustomerRequest request);
    CustomerResponse deactivateCustomer(UUID id);
    CustomerResponse activateCustomer(UUID id);
}
